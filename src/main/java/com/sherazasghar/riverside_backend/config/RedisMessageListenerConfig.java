package com.sherazasghar.riverside_backend.config;

import com.sherazasghar.riverside_backend.hanlders.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;


@Configuration
public class RedisMessageListenerConfig {


    @Autowired
    private RedisMessageSubscriber subscriber;


    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new MessageListenerAdapter(subscriber), new org.springframework.data.redis.listener.PatternTopic("rooms:events"));
        return container;
    }
}