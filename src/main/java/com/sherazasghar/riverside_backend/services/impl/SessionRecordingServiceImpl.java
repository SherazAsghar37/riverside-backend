package com.sherazasghar.riverside_backend.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.entities.SessionRecordings;
import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import com.sherazasghar.riverside_backend.exceptions.SessionNotFoundException;
import com.sherazasghar.riverside_backend.exceptions.SessionRecordingNotFoundException;
import com.sherazasghar.riverside_backend.repositories.SessionRecordingsRepository;
import com.sherazasghar.riverside_backend.repositories.SessionRepository;
import com.sherazasghar.riverside_backend.services.SessionRecordingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionRecordingServiceImpl implements SessionRecordingService {
    private final SessionRepository sessionRepository;
    private final SessionRecordingsRepository sessionRecordingsRepository;
    private final WebSocketService webSocketService;
    private final RoomService roomService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public SessionRecordings startRecording(UUID userId, String sessionCode) {
        Session session = sessionRepository.findBySessionCodeAndHostId(sessionCode, userId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found or user is not the host"));

        SessionRecordings sessionRecordings = sessionRecordingsRepository.findLastBySessionIdAndIsConcluded(session.getId(),false)
                .orElse(null);

        if(sessionRecordings==null){
            sessionRecordings = new SessionRecordings();
            sessionRecordings.setSession(session);
            sessionRecordings.setIsConcluded(false);
            session.setStatus(SessionStatusEnum.RECORDING);
            sessionRepository.save(session);
            sessionRecordings = sessionRecordingsRepository.save(sessionRecordings);

            try {
                webSocketService.broadcastMessage(mapper.writeValueAsString(Map.of(
                        "type","startRecording",
                        "data",Map.of("id", sessionRecordings.getId()),
                        "roomId", session.getId(),"excludeCurrentUser", false
                )));
                roomService.addSessionRecordingToRoom(session.getId().toString(),sessionRecordings.getId().toString());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return sessionRecordings;
    }

    @Override
    public SessionRecordings stopRecording(UUID userId, String sessionCode) {
            try{
                Session session = sessionRepository.findBySessionCodeAndHostId(sessionCode, userId)
                        .orElseThrow(() -> new SessionNotFoundException("Session not found or user is not the host"));

                session.setStatus(SessionStatusEnum.ONGOING);
                sessionRepository.save(session);

                SessionRecordings sessionRecordings = sessionRecordingsRepository.findLastBySessionIdAndIsConcluded(session.getId(),false)
                        .orElseThrow(() -> new SessionRecordingNotFoundException("Recording not found for the session"));

                sessionRecordings.setIsConcluded(true);
                sessionRecordingsRepository.save(sessionRecordings);

                webSocketService.broadcastMessage(mapper.writeValueAsString(Map.of(
                        "type","stopRecording",
                        "data",Map.of("id", sessionRecordings.getId(),"userId", userId,"isHost",(userId==session.getHost().getId())),
                        "roomId", session.getId(),"excludeCurrentUser", false
                )));
                roomService.removeSessionRecordingFromRoom(sessionRecordings.getId().toString());

                return sessionRecordings;
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
    }
}
