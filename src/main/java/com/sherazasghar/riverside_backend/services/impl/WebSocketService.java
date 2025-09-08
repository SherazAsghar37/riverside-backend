package com.sherazasghar.riverside_backend.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherazasghar.riverside_backend.services.JwtService;
import com.sherazasghar.riverside_backend.services.SessionParticipantService;
import com.sherazasghar.riverside_backend.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final RoomService roomService;
    private final SessionParticipantService sessionParticipantService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();


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

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(Map.of("type","joined"))));
        }catch ( Exception e){
            throw new RuntimeException(e);
        }
    }

    public void leaveSession(WebSocketSession session) {
        try{
            UUID userId =  UUID.fromString((String) session.getAttributes().get("userId"));
            UUID sessionId =  UUID.fromString((String) session.getAttributes().get("sessionId"));

            roomService.removeUserFromRoom(session.getId());
            sessionParticipantService.removeParticipantFromSession(sessionId, userId);
        }catch ( JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    public void storeSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void broadcastMessage(String sessionId, String payload) {
        roomService.publishToRoom(payload);
    }

    public void sendToLocalSession(String sessionId, String payload){
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
