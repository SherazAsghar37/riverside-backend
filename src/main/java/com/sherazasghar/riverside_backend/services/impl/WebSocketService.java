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
            mediasoupService.createRouter(objectMapper.writeValueAsString(Map.of("roomId",sessionId)));
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
            roomService.removeUserFromRoom(session.getId());


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
                        "userId",(String) session.getAttributes().get("userId"))));
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
    public void onTransportProducer(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

        mediasoupService.transportProducer(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "userId",(String) session.getAttributes().get("userId"),
                        "transportId", (String) payload.get("transportId"),
                        "sessionId", session.getId(),
                        "kind", (String) payload.get("kind"),
                        "rtpParameters", (String) payload.get("rtpParameters")
                        )));
    }

    public void onTransportConsumer(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

        mediasoupService.transportConsumer(objectMapper.writeValueAsString(
                Map.of("roomId",(String) session.getAttributes().get("sessionId"),
                        "userId",(String) session.getAttributes().get("userId"),
                        "transportId", (String) payload.get("transportId"),
                        "producerId", (String) payload.get("producerId"),
                        "kind", (String) payload.get("kind"),
                        "rtpCapabilities", (String) payload.get("rtpCapabilities")
                )));
    }

    public void onCloseProducer(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

        roomService.removeProducerFromSession(session.getId(), (String) payload.get("producerId"));
    }

    public void storeSession(WebSocketSession session){
        sessions.put(session.getId(), session);
    }

    public void broadcastMessage( String payload) {
        roomService.publishToRoom(payload);
    }

    public void sendToLocalSessionByUserId(String userId, String payload){
        WebSocketSession session = userToSession.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (Exception ex) {
                throw new RuntimeException("Unable to send message to user: " + userId, ex);
            }
        }
    }

    public void sendToLocalSession(String sessionId, String payload,Boolean excludeCurrentUser) throws JsonProcessingException {

        Map<String, Object> map = objectMapper.readValue(payload, Map.class);
        Map<String, Object> data = ( Map<String, Object>) map.get("data");
        String userId = data.get("userId").toString();
        WebSocketSession mySessionId = userToSession.get(userId);

        if(excludeCurrentUser && mySessionId.getId().equals(sessionId)){
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


}
