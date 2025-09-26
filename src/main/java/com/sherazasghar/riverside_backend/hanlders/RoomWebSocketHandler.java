package com.sherazasghar.riverside_backend.hanlders;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


import com.sherazasghar.riverside_backend.services.JwtService;
import com.sherazasghar.riverside_backend.services.impl.JwtServiceImpl;
import com.sherazasghar.riverside_backend.services.impl.RoomService;
import com.sherazasghar.riverside_backend.services.impl.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import com.fasterxml.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
public class RoomWebSocketHandler extends TextWebSocketHandler {
    private final RoomService roomService;
    private final WebSocketService webSocketService;
    private final JwtService jwtService;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
         final  Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);

         String type = (String) payload.get("type");
         String sessionId = (String) payload.get("sessionId");
         String userId = (String) payload.get("userId");

        switch (type) {
            case "join" ->  webSocketService.joinSession(session, message);
            case "leave" -> webSocketService.leaveSession(session);
            case "getRTPCapabilities"-> webSocketService.onGetRTPCapabilitiesRequest(session);
            case "createSendTransport"-> webSocketService.onCreateSendTransportRequest(session);
            case "connectTransport" -> webSocketService.onConnectTransport(session,message);
            case "transportProducer"-> webSocketService.onTransportProducer(session,message);
            case "createReceiveTransport"-> webSocketService.onCreateReceiveTransportRequest(session);
            case "transportConsumer"-> webSocketService.onTransportConsumer(session,message);
            case "resumeReceiver"-> webSocketService.onResumeReceiver(session,message);
            case "pauseReceiver"-> webSocketService.onPauseReceiver(session,message);
            case "closeProducer"-> webSocketService.onCloseProducer(session,message);
            case "message" -> webSocketService.broadcastMessage( message.getPayload());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            webSocketService.leaveSession(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        webSocketService.storeSession(session);
    }




}
