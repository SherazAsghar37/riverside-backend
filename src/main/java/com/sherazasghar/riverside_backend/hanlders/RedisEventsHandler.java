package com.sherazasghar.riverside_backend.hanlders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.impl.RoomService;
import com.sherazasghar.riverside_backend.services.impl.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class RedisEventsHandler {
    @Autowired
    private  RoomService roomService;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    public void onBroadcastEvent(String payload) {
        try {
            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String roomId = (String) map.get("roomId").toString();

            if (roomId == null) return;

            Boolean excludeCurrentUser = (Boolean) map.containsKey("excludeCurrentUser") && (Boolean) map.get("excludeCurrentUser");

            // fetch all sessionIds in room and deliver to local sessions
            for (String sessionId : roomService.getSessionIdsInRoom(roomId)) {
                webSocketService.sendToLocalSession(sessionId, payload,excludeCurrentUser);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Failed to broadcast event", ex);
        }
    }

    public void onRouterRTPCapabilities(String payload) {
        try {
            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String userId = map.get("userId").toString();

            webSocketService.sendToLocalSessionByUserId(userId, mapper.writeValueAsString(Map.of("type","routerRtpCapabilities","data", map.get("rtpCapabilities"))));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get router rtp capabilities", ex);
        }
    }
    public void onCreateSendTransport(String payload) {
        try {
            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String userId = map.get("userId").toString();

            webSocketService.sendToLocalSessionByUserId(userId, mapper.writeValueAsString(Map.of("type","sendTransportCreated","data", map.get("transportOptions"))));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create sen transport", ex);
        }
    }
    public void onConnectTransport(String payload) {
        try {
            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String userId = map.get("userId").toString();
            String userType = map.get("userType").toString();

            webSocketService.sendToLocalSessionByUserId(userId, mapper.writeValueAsString(Map.of("type", (userType.equals("sender") ? "senderTransportConnected" : "consumerTransportConnected"))));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to connect transport", ex);
        }
    }
    public void onProducerCreated(String payload) {
        try {
            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String userId = map.get("userId").toString();
            String sessionId = map.get("sessionId").toString();
            String producerId  = map.get("id").toString();

            final Optional<User> user = userRepository.findById(UUID.fromString(userId));
            map.put("userName",user.get().getName());

            webSocketService.sendToLocalSessionByUserId(userId, mapper.writeValueAsString(Map.of("type","producerCreated","data", map.get("id"))));

            webSocketService.broadcastMessage(mapper.writeValueAsString(Map.of(
                    "type","newProducerJoined",
                    "data",map,
                    "roomId", map.get("roomId"),"excludeCurrentUser", true
            )));
            roomService.addProducerToSession(sessionId, producerId, mapper.writeValueAsString(map));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create producer", ex);
        }
    }

    public void onConsumerCreated(String payload) {
        try {
            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String participantId = map.get("participantId").toString();
            String userId = map.get("userId").toString();

            webSocketService.sendToLocalSessionByUserId(userId, mapper.writeValueAsString(Map.of("type","consumerCreated","data", map,"participantId",participantId
            )));

        } catch (Exception ex) {
            throw new RuntimeException("Failed to create consumer", ex);
        }
    }

    public void onCreateReceiveTransport(String payload) {
        try {
            Map<String, Object> map = mapper.readValue(payload, Map.class);
            String userId = map.get("userId").toString();
            String roomId = map.get("roomId").toString();
            String sessionId = map.get("sessionId").toString();
            final List<String> producers = roomService.getAllProducersInRoom(roomId,sessionId);


            webSocketService.sendToLocalSessionByUserId(userId, mapper.writeValueAsString(Map.of("type","receiveTransportCreated","data", map.get("transportOptions"),"producers", producers)));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create receive transport", ex);
        }
    }
}
