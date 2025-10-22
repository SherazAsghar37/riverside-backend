package com.sherazasghar.riverside_backend.services;

import com.sherazasghar.riverside_backend.constants.RedisChannels;

public interface MediasoupService {

    public void getRouterRtpCapabilities(String roomId);

    public void createSendTransport(String payload);

    public void createReceiveTransport(String payload) ;

    public void connectTransport(String payload) ;

    public void createProducer(String payload) ;

    public void createConsumer(String payload) ;

    public void resumeReceiver(String payload) ;

    public void pauseReceiver(String payload) ;

    public void userDisconnected(String payload) ;

    public void sessionEnded(String payload) ;
}
