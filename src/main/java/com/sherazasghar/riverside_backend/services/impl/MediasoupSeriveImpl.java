package com.sherazasghar.riverside_backend.services.impl;

import com.sherazasghar.riverside_backend.constants.RedisChannels;
import com.sherazasghar.riverside_backend.services.MediasoupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediasoupSeriveImpl implements MediasoupService {
    @Autowired
    private RoomService roomService;

    @Override
    public void createRouter(String roomId) {
        roomService.publishToChannel(RedisChannels.REQUEST_CREATE_ROUTER,roomId);
    }

    @Override
    public void getRouterRtpCapabilities(String payload) {
        roomService.publishToChannel(RedisChannels.REQUEST_GET_ROUTER_RTP_CAPABILITIES,payload);
    }

    @Override
    public void createSendTransport(String payload) {
        roomService.publishToChannel(RedisChannels.REQUEST_CREATE_SEND_TRANSPORT,payload);
    }

    @Override
    public void createReceiveTransport(String payload) {
        roomService.publishToChannel(RedisChannels.REQUEST_CREATE_RECV_TRANSPORT,payload);
    }

    @Override
    public void connectTransport(String payload) {
        roomService.publishToChannel(RedisChannels.REQUEST_CONNECT_TRANSPORT,payload);
    }

    @Override
    public void transportProducer(String payload) {
        roomService.publishToChannel(RedisChannels.REQUEST_TRANSPORT_PRODUCER,payload);
    }

    @Override
    public void transportConsumer(String payload) {
        roomService.publishToChannel(RedisChannels.REQUEST_TRANSPORT_CONSUMER,payload);
    }

    @Override
    public void resumeReceiver(String payload) {
        roomService.publishToChannel(RedisChannels.REQUEST_RESUME,payload);
    }

    @Override
    public void pauseReceiver(String payload) {
        roomService.publishToChannel(RedisChannels.REQUEST_PAUSE,payload);
    }
}
