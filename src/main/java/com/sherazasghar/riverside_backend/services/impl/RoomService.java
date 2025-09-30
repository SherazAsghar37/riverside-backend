package com.sherazasghar.riverside_backend.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private String producerKey(String producerId){
        return "producer:" + producerId + ":session";
    }
    private String sessionProducerKey(String sessionId){
        return "session:" + sessionId + ":producers";
    }
    private String producerIdToProducerInfoKey(String producerId){
        return "producer:" + producerId + ":info";
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

    public void publishToChannel(String channel, String payload) {
        redisTemplate.convertAndSend(channel, payload);
    }

    public void addUserToRoom(String roomId, String sessionId) throws JsonProcessingException {
        redisTemplate.opsForSet().add(roomKey(roomId), sessionId);
        redisTemplate.opsForValue().set(sessionKey(sessionId), roomId);

        Map<String,Object> event = Map.of(
                "event", "join",
                "roomId", sessionId
        );
        publishToRoom(objectMapper.writeValueAsString(event));
    }

    public void removeUserFromRoom(String sessionId, String userId, String roomId) throws JsonProcessingException {

        final String roomKey = roomKey(roomId);

        redisTemplate.opsForSet().remove(roomKey, sessionId);
        redisTemplate.delete(sessionKey(sessionId));

        removeAllProducersFromSession(sessionId);
        Map<String,Object> event = Map.of(
                "event", "leave",
                "roomId", roomId,
                "participantId",userId,
                "type","userDisconnected"
        );
        publishToRoom(objectMapper.writeValueAsString(event));
    }

    public Set<String> getSessionIdsInRoom(String roomId) {
        final String roomKey = roomKey(roomId);
        Set<Object> s = redisTemplate.opsForSet().members(roomKey);
        if (s == null) return new HashSet<>();
        return s.stream().map(Object::toString).collect(Collectors.toSet());
    }

    public void addProducerToSession( String sessionId, String producerId, String producerInfo) {
        redisTemplate.opsForValue().set(producerIdToProducerInfoKey(producerId), producerInfo);
        redisTemplate.opsForSet().add(sessionProducerKey(sessionId), producerId);
        redisTemplate.opsForValue().set(producerKey(producerId), sessionId);
    }

    public void removeProducerFromSession(String sessionId, String producerId) {
        redisTemplate.opsForSet().remove(sessionProducerKey(sessionId), producerId);
        redisTemplate.delete(producerKey(producerId));
        redisTemplate.delete(producerIdToProducerInfoKey(producerId));
    }

    public void removeAllProducersFromSession(String sessionId) {
        Set<Object> producers = redisTemplate.opsForSet().members(sessionProducerKey(sessionId));
        if (producers != null) {
            for (Object producerId : producers) {
                redisTemplate.delete(producerKey(producerId.toString()));
                redisTemplate.delete(producerIdToProducerInfoKey(producerId.toString()));
            }
        }
        redisTemplate.delete(sessionProducerKey(sessionId));
    }

    public List<String> getAllProducersInRoom(String roomId,String userSessionId) {
        Set<String> sessionIds = getSessionIdsInRoom(roomId);
        return sessionIds.stream()
                .flatMap(sessionId -> {
                    if(sessionId.equals(userSessionId)){
                        return Stream.<String>of();
                    }
                    Set<Object> producers = redisTemplate.opsForSet()
                            .members(sessionProducerKey(sessionId));

                    if (producers != null) {
                       return producers.stream()
                                .map(producer -> (String) redisTemplate.opsForValue()
                                        .get(producerIdToProducerInfoKey((String) producer)));

                    } else {
                        return  Set.<String>of().stream();
                    }
                })
                .collect(Collectors.toList());
    }
}