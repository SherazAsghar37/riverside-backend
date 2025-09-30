package com.sherazasghar.riverside_backend.constants;

public class RedisChannels {
    public static final String ROOM_EVENT = "rooms:events";

    public static final String REQUEST_CREATE_ROUTER = "request:create-router";
    public static final String REQUEST_GET_ROUTER_RTP_CAPABILITIES = "request:get-router-rtp-capabilities";
    public static final String REQUEST_CREATE_SEND_TRANSPORT = "request:create-send-transport";
    public static final String REQUEST_CREATE_RECV_TRANSPORT = "request:create-recv-transport";

    public static final String REQUEST_CONNECT_TRANSPORT = "request:connect-transport";
    public static final String REQUEST_CREATE_PRODUCER = "request:create-producer";
    public static final String REQUEST_CREATE_CONSUMER = "request:create-consumer";

    public static final String REQUEST_PAUSE = "request:pause";
    public static final String REQUEST_RESUME = "request:resume";

    public static final String RESPONSE_GET_ROUTER_RTP_CAPABILITIES  = "response:get-router-rtp-capabilities";
    public static final String RESPONSE_CREATE_SEND_TRANSPORT  = "response:create-send-transport";
    public static final String RESPONSE_CREATE_RECV_TRANSPORT  = "response:create-recv-transport";
    public static final String RESPONSE_CONNECT_TRANSPORT  = "response:connect-transport";
    public static final String RESPONSE_CREATE_PRODUCER  = "response:create-producer";
    public static final String RESPONSE_CREATE_CONSUMER  = "response:create-consumer";

}