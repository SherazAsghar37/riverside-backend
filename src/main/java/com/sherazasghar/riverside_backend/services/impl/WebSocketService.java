package com.sherazasghar.riverside_backend.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherazasghar.riverside_backend.services.JwtService;
import com.sherazasghar.riverside_backend.services.MediasoupService;
import com.sherazasghar.riverside_backend.services.SessionParticipantService;
import com.sherazasghar.riverside_backend.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final RoomService roomService;
    private final SessionParticipantService sessionParticipantService;
    private final JwtService jwtService;
    private final MediasoupService mediasoupService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> userToSession = new ConcurrentHashMap<>();


    public void joinSession(WebSocketSession session, TextMessage message) {
        try{
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

            String token = (String) payload.get("token");
            String userId = jwtService.parseTokenAndGetUserId(token);
            String sessionId = (String) payload.get("sessionId");

            if(userId == null){
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("type","error", "message","Invalid Token"))));
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid token"));
                return;
            }

            session.getAttributes().put("userId", userId);
            session.getAttributes().put("sessionId", sessionId);

            //room id is same as session id of database.
            roomService.addUserToRoom(sessionId,session.getId());
            sessionParticipantService.addParticipantToSession(UUID.fromString(sessionId), UUID.fromString(userId));

            userToSession.put(userId, session);
            sessions.put(session.getId(), session);


            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("type","joined"))));
        }catch ( Exception e){
            throw new RuntimeException(e);
        }
    }

    public void leaveSession(WebSocketSession session) {
        try{
            UUID userId=null;
            UUID sessionId=null;

            sessions.remove(session.getId());


            if( session.getAttributes().get("userId") != null){
                 userId =  UUID.fromString((String) session.getAttributes().get("userId"));
                userToSession.remove(userId.toString());
            }

            if( session.getAttributes().get("sessionId") != null){
                 sessionId =  UUID.fromString((String) session.getAttributes().get("sessionId"));
            }

            if( session.getAttributes().get("userId") == null || session.getAttributes().get("sessionId") == null){
                return;
            }

            sessionParticipantService.removeParticipantFromSession(sessionId, userId);

            if(roomService.doesRoomExist(sessionId.toString())){
                roomService.removeUserFromRoom(session.getId(), userId.toString(),sessionId.toString());
                mediasoupService.userDisconnected(objectMapper.writeValueAsString(
                        Map.of("roomId",sessionId.toString(),
                                "userId",userId.toString()
                        )));
            }
        }catch ( JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    public void onGetRTPCapabilitiesRequest(WebSocketSession session) throws JsonProcessingException {
        mediasoupService.getRouterRtpCapabilities(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "userId",(String) session.getAttributes().get("userId"))));
    }
    public void onCreateSendTransportRequest(WebSocketSession session) throws JsonProcessingException {
        mediasoupService.createSendTransport(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "userId",(String) session.getAttributes().get("userId"))));
    }
    public void onCreateReceiveTransportRequest(WebSocketSession session) throws JsonProcessingException {
        mediasoupService.createReceiveTransport(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "userId",(String) session.getAttributes().get("userId"),"sessionId", session.getId())));
    }
    public void onConnectTransport(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

        mediasoupService.connectTransport(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "userId",(String) session.getAttributes().get("userId"),
                        "transportId", (String) payload.get("transportId"),
                        "userType", (String) payload.get("userType"),
                        "dtlsParameters", (String) payload.get("dtlsParameters")
                        )));
    }

    public void onResumeReceiver(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        mediasoupService.resumeReceiver(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "consumerId", (String) payload.get("consumerId"),
                        "userId",(String) session.getAttributes().get("userId"))));
    }
    public void onPauseReceiver(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        mediasoupService.pauseReceiver(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "consumerId", (String) payload.get("consumerId"),
                        "userId",(String) session.getAttributes().get("userId"))));
    }

    public void onPauseProducer(WebSocketSession session, TextMessage message) throws JsonProcessingException {
          Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        roomService.setProducerStatus(payload.get("producerId").toString(),false);
        String roomId = session.getAttributes().get("sessionId").toString();
        String userId = session.getAttributes().get("userId").toString();
        payload.put("userId", userId);

        broadcastMessage(objectMapper.writeValueAsString(Map.of(
                "type","producerPaused",
                "data",payload,
                "roomId", roomId,"excludeCurrentUser", true
        )));

    }

    public void onResumeProducer(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        final String producerId = payload.get("producerId").toString();
        roomService.setProducerStatus(producerId,true);

        String sessionId = session.getAttributes().get("sessionId").toString();
        String userId = session.getAttributes().get("userId").toString();

        final  String producer = roomService.getProducerInfo(payload.get("producerId").toString());
        final Map<String,Object> data = objectMapper.readValue(producer, Map.class);
        data.put("producerId", payload.get("producerId").toString());
        data.put("userId", userId);

        broadcastMessage(objectMapper.writeValueAsString(Map.of(
                "type","newProducerJoined",
                "data",data,
                "roomId", sessionId,"excludeCurrentUser", true
        )));

    }

    public void onCreateProducer(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

        mediasoupService.createProducer(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "userId",(String) session.getAttributes().get("userId"),
                        "transportId", (String) payload.get("transportId"),
                        "sessionId", session.getId(),
                        "kind", (String) payload.get("kind"),
                        "appData",(String) payload.get("appData"),
                        "rtpParameters", (String) payload.get("rtpParameters")
                        )));
    }

    public void onCreateConsumer(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

        System.out.println("RoomId "+ (String) session.getAttributes().get("sessionId"));
        System.out.println("userId "+ (String) session.getAttributes().get("userId"));
        System.out.println("transportId "+ (String) session.getAttributes().get("transportId"));
        System.out.println("producerId "+ (String) payload.get("producerId"));
        System.out.println("participantId "+ (String) payload.get("participantId"));
        System.out.println("kind "+ (String) payload.get("kind"));
        System.out.println("rtpCapabilities "+ (String) payload.get("rtpCapabilities"));
        System.out.println("sessionId "+ session.getId());
        System.out.println("appData "+ (String) payload.get("appData"));
        System.out.println("userName "+ (String) payload.get("userName"));
        System.out.println("-------------------");

        mediasoupService.createConsumer(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "userId",(String) session.getAttributes().get("userId"),
                        "participantId", (String) payload.get("participantId"),
                        "transportId", (String) payload.get("transportId"),
                        "producerId", (String) payload.get("producerId"),
                        "userName" , (String) payload.get("userName"),
                        "kind", (String) payload.get("kind"),
                        "sessionId", session.getId(),
                        "appData",(String) payload.get("appData"),
                        "rtpCapabilities", (String) payload.get("rtpCapabilities")
                )));
    }

    public void onCloseProducer(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String roomId = session.getAttributes().get("sessionId").toString();

        roomService.removeProducerFromSession(session.getId(), (String) payload.get("producerId"));
        payload.put("userId", session.getAttributes().get("userId").toString());
        broadcastMessage(objectMapper.writeValueAsString(Map.of(
                "type","producerPaused",
                "data",payload,
                "roomId", roomId,"excludeCurrentUser", true
        )));

    }

    public void broadcastMessage( String payload) {
        roomService.publishToRoom(payload);
    }

    public void sendToLocalSessionByUserId(String userId, String payload){
        WebSocketSession session = userToSession.get(userId);
        if (session != null && session.isOpen()) {
            synchronized (session) {
                try {
                    session.sendMessage(new TextMessage(payload));
                } catch (Exception ex) {
                    throw new RuntimeException("Unable to send message to user: " + userId, ex);
                }
            }
        }
    }

    public void sendToLocalSession(String sessionId, String payload,Boolean excludeCurrentUser) throws JsonProcessingException {

        Map<String, Object> map = objectMapper.readValue(payload, Map.class);
        Map<String, Object> data = ( Map<String, Object>) map.get("data");
        String userId=null;
        WebSocketSession mySessionId=null;

        if(excludeCurrentUser){
             userId = data.get("userId").toString();
             mySessionId = userToSession.get(userId);
        }

        if(mySessionId!=null&& mySessionId.getId().equals(sessionId)){
            return;
        }
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (Exception ex) {
                throw new RuntimeException("Unable to send message to session: " + sessionId, ex);
            }
        }
    }

    public void onSessionEnded(UUID sessionId) {
        try{
            roomService.endRoom(sessionId.toString());
            mediasoupService.sessionEnded(objectMapper.writeValueAsString(
                    Map.of("roomId",sessionId.toString()
                    )));
        }catch ( Exception e){
            throw new RuntimeException(e);
        }
    }



}
