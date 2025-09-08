package com.sherazasghar.riverside_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {
     @Value("${spring.data.redis.host}")
     private String host;

     @Value("${spring.data.redis.port}")
     private int port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host,port );
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Object> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new StringRedisSerializer());
        return tpl;
    }

    @Bean
    public ChannelTopic roomTopic() {
        return new ChannelTopic("rooms:events");
    }
}