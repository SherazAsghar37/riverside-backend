package com.sherazasghar.riverside_backend.services.impl;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;


@Service
public class RoomService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic roomTopic;
    @Autowired
    private ObjectMapper objectMapper;

    private String roomKey(String roomId) {
        return "room:" + roomId + ":sessions";
    }

    private String sessionKey(String sessionId) {
        return "session:" + sessionId + ":room";
    }

    private String getRoomIdFromSessionId(String sessionId) {
        Object val = redisTemplate.opsForValue().get(sessionKey(sessionId));
        if( val == null ){
            return null;
        }
        return roomKey((String) val);
    }

    public void publishToRoom( String payload) {
        redisTemplate.convertAndSend(roomTopic.getTopic(), payload);
    }

    public void addUserToRoom(String roomId, String sessionId) throws JsonProcessingException {
        redisTemplate.opsForSet().add(roomKey(roomId), sessionId);
        redisTemplate.opsForValue().set(sessionKey(sessionId), roomId);

        Map<String,Object> event = Map.of(
                "event", "join",
                "sessionId", sessionId
        );
        publishToRoom(objectMapper.writeValueAsString(event));
    }

    public void removeUserFromRoom(String sessionId) throws JsonProcessingException {

        final String roomKey = getRoomIdFromSessionId(sessionId);
        if(roomKey != null) {
            redisTemplate.opsForSet().remove(roomKey, sessionId);
            redisTemplate.delete(sessionKey(sessionId));
        }
        Map<String,Object> event = Map.of(
                "event", "leave",
                "sessionId", sessionId
        );
        publishToRoom(objectMapper.writeValueAsString(event));
    }

    public Set<String> getSessionIdsInRoom(String roomId) {
        final String roomKey = roomKey(roomId);
        Set<Object> s = redisTemplate.opsForSet().members(roomKey);
        if (s == null) return new HashSet<>();
        return s.stream().map(Object::toString).collect(Collectors.toSet());
    }

}