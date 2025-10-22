package com.sherazasghar.riverside_backend.services.impl;

import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import com.sherazasghar.riverside_backend.domain.requests.SessionCreateRequest;
import com.sherazasghar.riverside_backend.exceptions.*;
import com.sherazasghar.riverside_backend.hanlders.RoomWebSocketHandler;
import com.sherazasghar.riverside_backend.repositories.SessionRepository;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.SessionService;
import com.sherazasghar.riverside_backend.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
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
    public Session getSessionFromSessionCode(String sessionCode) {
        Session session = sessionRepository.findBySessionCode(sessionCode.trim()).orElseThrow(() -> new SessionNotFoundException("Session with code " + sessionCode + " not found"));
        return sessionStatusValidation(sessionCode, session);
    }

    @Override
    public Session joinSessionAsHost(String sessionCode, UUID hostId) {
        Session session = sessionRepository.findBySessionCodeAndHostId(sessionCode,hostId).orElseThrow(() -> new SessionNotFoundException("Session with code " + sessionCode + " not found"));
        return sessionStatusValidation(sessionCode, session);
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
    public Session sessionDetailsFromSessionCode(String sessionCode) {
        Session session = sessionRepository.findBySessionCode(sessionCode).orElseThrow(() -> new SessionNotFoundException("Session with code " + sessionCode + " not found"));
        return session;
    }

    @Override
    public Session endSession(UUID sessionId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException("Session with id " + sessionId + " not found"));
           session.setStatus(SessionStatusEnum.COMPLETED);
           sessionRepository.save(session);
            webSocketService.onSessionEnded(sessionId);
        return session;
    }


}
