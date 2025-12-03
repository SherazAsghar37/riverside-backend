package com.sherazasghar.riverside_backend.services.impl;

import java.util.*;
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
    private String producerToProducerStatus(String producerId){
        return "producer:" + producerId + ":status";
    }
    private String roomToSessionRecordingId(String roomId){
        return "SessionRecording:" + roomId + ":room";
    }
    private String sessionRecordingIdToRoomKey(String sessionRecordingId){
        return "room:" + sessionRecordingId + ":SessionRecording";
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
                "roomId", roomId
                //changed from session id
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
        setProducerStatus(producerId,true);
    }

    public void removeProducerFromSession(String sessionId, String producerId) {
        redisTemplate.opsForSet().remove(sessionProducerKey(sessionId), producerId);
        redisTemplate.delete(producerKey(producerId));
        redisTemplate.delete(producerIdToProducerInfoKey(producerId));
        redisTemplate.delete(producerToProducerStatus(producerId));
    }

    public void removeAllProducersFromSession(String sessionId) {
        Set<Object> producers = redisTemplate.opsForSet().members(sessionProducerKey(sessionId));
        if (producers != null) {
            for (Object producerId : producers) {
                redisTemplate.delete(producerKey(producerId.toString()));
                redisTemplate.delete(producerIdToProducerInfoKey(producerId.toString()));
                redisTemplate.delete(producerToProducerStatus(producerId.toString()));
            }
        }
        redisTemplate.delete(sessionProducerKey(sessionId));
    }

    public List<String> getAllProducersInRoom(String roomId, String userSessionId) {
        Set<String> sessionIds = getSessionIdsInRoom(roomId);
        if (sessionIds == null || sessionIds.isEmpty()) {
            return Collections.emptyList();
        }

        return sessionIds.stream()
                .filter(sessionId -> !sessionId.equals(userSessionId))
                .flatMap(sessionId -> {
                    Set<Object> producers = redisTemplate.opsForSet()
                            .members(sessionProducerKey(sessionId));
                    if (producers == null || producers.isEmpty()) {
                        return Stream.empty();
                    }
                    return producers.stream()
                            .map(Object::toString)
                            .map(producerId -> {
                                Boolean status = getProducerStatus(producerId);
                                if (Boolean.TRUE.equals(status)) {
                                    Object info = redisTemplate.opsForValue()
                                            .get(producerIdToProducerInfoKey(producerId));
                                    return info == null ? null : info.toString();
                                }
                                return null;
                            })
                            .filter(Objects::nonNull);
                })
                .collect(Collectors.toList());
    }

    public void setProducerStatus(String producerId, Boolean status) {
        redisTemplate.opsForValue().set(producerToProducerStatus(producerId), Boolean.toString(status));
    }
    public Boolean getProducerStatus(String producerId) {
        Object val = redisTemplate.opsForValue().get(producerToProducerStatus(producerId));
        if( val == null ){
            return null;
        }
        return Boolean.valueOf((String) val) ;
    }

    public String getProducerInfo(String producerId) {
        Object val = redisTemplate.opsForValue().get(producerIdToProducerInfoKey(producerId));
        if( val == null ){
            return null;
        }
        return (String) val;
    }

    public void endRoom(String roomId) throws JsonProcessingException {
        Map<String,Object> event = Map.of(
                "event", "session-ended",
                "roomId", roomId,
                "type","sessionEnded"
        );
        publishToRoom(objectMapper.writeValueAsString(event));

        Set<String> sessionIds = getSessionIdsInRoom(roomId);
        if (sessionIds != null) {
            for (String sessionId : sessionIds) {
                removeAllProducersFromSession(sessionId);
                redisTemplate.delete(sessionKey(sessionId));
                redisTemplate.delete(sessionProducerKey(sessionId));
            }
        }
        // Remove the room's session set
        redisTemplate.delete(roomKey(roomId));
    }

    public Boolean doesRoomExist(String roomId) {
        String roomKey = roomKey(roomId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(roomKey));
    }

    public void addSessionRecordingToRoom(String roomId, String sessionRecordingId) {
        redisTemplate.opsForValue().set(roomToSessionRecordingId(roomId), sessionRecordingId);
        redisTemplate.opsForValue().set(sessionRecordingIdToRoomKey(sessionRecordingId), roomId);
    }

    public String getSessionRecordingIdFromRoomId(String roomId) {
        Object value =
                redisTemplate.opsForValue().get(roomToSessionRecordingId(roomId));
        return value == null ? null : value.toString();
    }


    public void removeSessionRecordingFromRoom(String sessionRecordingId) {
        Object roomId = redisTemplate.opsForValue().get(sessionRecordingIdToRoomKey(sessionRecordingId));
        if (roomId != null) {
            redisTemplate.delete(roomToSessionRecordingId(roomId.toString()));
        }
        redisTemplate.delete(sessionRecordingIdToRoomKey(sessionRecordingId));
    }
}