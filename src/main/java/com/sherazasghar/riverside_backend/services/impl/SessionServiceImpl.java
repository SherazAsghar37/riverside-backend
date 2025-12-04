package com.sherazasghar.riverside_backend.services.impl;

import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.entities.SessionRecordings;
import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import com.sherazasghar.riverside_backend.domain.requests.SessionCreateRequest;
import com.sherazasghar.riverside_backend.exceptions.*;
import com.sherazasghar.riverside_backend.repositories.SessionRecordingsRepository;
import com.sherazasghar.riverside_backend.repositories.SessionRepository;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.ParticipantsRecordingService;
import com.sherazasghar.riverside_backend.services.SessionRecordingService;
import com.sherazasghar.riverside_backend.services.SessionService;
import com.sherazasghar.riverside_backend.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final WebSocketService webSocketService;
    private final SessionRecordingService sessionRecordingService;
    private final ParticipantsRecordingService participantsRecordingService;

    @Override
    public Session createSession(UUID hostId, SessionCreateRequest request) {
        User user  = userRepository.findById(hostId).orElseThrow(() -> new UserNotFoundException("User with id " + hostId+" not found"));
        Session session = new Session();
        session.setHost(user);
        session.setName(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toString());
        session.setSessionCode(SessionUtils.generateSessionCode());

        if(request.getScheduledAt() != null) {
            session.setScheduledAt(request.getScheduledAt());
            session.setStatus(SessionStatusEnum.SCHEDULED);
        }

        return sessionRepository.save(session);
    }

    @Override
    public List<Session> listSessions(UUID hostId) {
        return sessionRepository.findByHostId(hostId);
    }

    @Override
    @Cacheable(value = "session", key = "#sessionCode", condition = "#result != null && (#result.status.name() == 'COMPLETED' || #result.status.name() == 'CANCELLED')")
    public Session sessionFormSessionCode(String sessionCode) {
        Session session = sessionRepository.findBySessionCode(sessionCode.trim()).orElseThrow(() -> new SessionNotFoundException("Session with code " + sessionCode + " not found"));
        return sessionStatusValidation(sessionCode, session);
    }

    @Override
    public Session joinSessionAsHost(String sessionCode, UUID hostId) {
        Session session = sessionRepository.findBySessionCodeAndHostId(sessionCode,hostId).orElseThrow(() -> new SessionNotFoundException("Session with code " + sessionCode + " not found"));
        return  sessionStatusValidation(sessionCode, session);

    }

    private Session sessionStatusValidation(String sessionCode, Session session) {
        if(session.getStatus() == SessionStatusEnum.COMPLETED ) {
            throw new SessionCompletedException("Session with code " + sessionCode + " is already completed" );
        }
        else if( session.getStatus() == SessionStatusEnum.CANCELLED) {
            throw new SessionCancelledException("Session with code " + sessionCode + " is cancelled" );
        }
        else if(session.getStatus() == SessionStatusEnum.SCHEDULED ){
           if(session.getScheduledAt().isAfter(Instant.now().atZone(ZoneOffset.UTC).toLocalDateTime())){
               throw new SessionNotStartedException("Session with code " + sessionCode + " is not active yet");
           }
           session.setStatus(SessionStatusEnum.ONGOING);
            return sessionRepository.save(session);
        }
        return session;
    }

    @Override
    @CacheEvict(value = "session", key = "#sessionCode")
    public Session endSession(String sessionCode) {
        Session session = sessionRepository.findBySessionCode(sessionCode).orElseThrow(() -> new SessionNotFoundException("Session with sessionCode " + sessionCode + " not found"));
           session.setStatus(SessionStatusEnum.COMPLETED);

           sessionRepository.save(session);
           webSocketService.onSessionEnded(session.getId());
           SessionRecordings sessionRecordings =  sessionRecordingService.stopRecording(session.getHost().getId(),sessionCode);
           participantsRecordingService.stopAllRecordingsBySessionRecordingId(sessionRecordings.getId());
        return session;
    }


}
