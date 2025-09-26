package com.sherazasghar.riverside_backend.hanlders;

import com.sherazasghar.riverside_backend.constants.RedisChannels;
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

    @Autowired
    private RedisEventsHandler redisEventsHandler;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());
            String channel = new String(message.getChannel());

            switch (channel){
                case RedisChannels.ROOM_EVENT -> redisEventsHandler.onBroadcastEvent(payload);
                case RedisChannels.RESPONSE_GET_ROUTER_RTP_CAPABILITIES ->redisEventsHandler.onRouterRTPCapabilities(payload);
                case RedisChannels.RESPONSE_CREATE_SEND_TRANSPORT -> redisEventsHandler.onCreateSendTransport(payload);
                case RedisChannels.RESPONSE_CONNECT_TRANSPORT -> redisEventsHandler.onConnectTransport(payload);
                case RedisChannels.RESPONSE_TRANSPORT_PRODUCER -> redisEventsHandler.onTransportProducer(payload);
                case RedisChannels.RESPONSE_CREATE_RECV_TRANSPORT -> redisEventsHandler.onCreateReceiveTransport(payload);
                case RedisChannels.RESPONSE_TRANSPORT_CONSUMER -> redisEventsHandler.onTransportConsumer(payload);
            }


        } catch (Exception ex) {
            throw new RuntimeException("Failed to process message", ex);
        }
    }
}