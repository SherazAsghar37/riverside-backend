package com.sherazasghar.riverside_backend.config;

import com.sherazasghar.riverside_backend.constants.RedisChannels;
import com.sherazasghar.riverside_backend.hanlders.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;


@Configuration
public class RedisMessageListenerConfig {


    @Autowired
    private RedisMessageSubscriber subscriber;


    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
            new MessageListenerAdapter(subscriber),
                List.of(
                        new PatternTopic(RedisChannels.ROOM_EVENT),
                        new PatternTopic(RedisChannels.RESPONSE_GET_ROUTER_RTP_CAPABILITIES),
                        new PatternTopic(RedisChannels.RESPONSE_CREATE_SEND_TRANSPORT),
                        new PatternTopic(RedisChannels.RESPONSE_CONNECT_TRANSPORT),
                        new PatternTopic(RedisChannels.RESPONSE_TRANSPORT_PRODUCER),
                        new PatternTopic(RedisChannels.RESPONSE_CREATE_RECV_TRANSPORT),
                        new PatternTopic(RedisChannels.RESPONSE_TRANSPORT_CONSUMER)
                )
        );
        return container;
    }
}