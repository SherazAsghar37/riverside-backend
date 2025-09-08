package com.sherazasghar.riverside_backend.hanlders;

import com.sherazasghar.riverside_backend.services.impl.RoomService;
import com.sherazasghar.riverside_backend.services.impl.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;


@Component
public class RedisMessageSubscriber implements MessageListener {
    @Autowired
    private RoomService roomService;

    @Autowired
    private WebSocketService webSocketService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());

            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String roomId = ((Map<String, Object>) map.get("content")).get("roomId").toString();

            if (roomId == null) return; // ignore

            // fetch all sessionIds in room and deliver to local sessions
            for (String sessionId : roomService.getSessionIdsInRoom(roomId)) {
                webSocketService.sendToLocalSession(sessionId, payload);
            }

        } catch (Exception ex) {
            throw new RuntimeException("Failed to process message", ex);
        }
    }
}