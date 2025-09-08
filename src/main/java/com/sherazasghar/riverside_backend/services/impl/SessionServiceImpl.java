package com.sherazasghar.riverside_backend.services.impl;

import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import com.sherazasghar.riverside_backend.domain.requests.SessionCreateRequest;
import com.sherazasghar.riverside_backend.exceptions.SessionNotFoundException;
import com.sherazasghar.riverside_backend.exceptions.UserNotFoundException;
import com.sherazasghar.riverside_backend.repositories.SessionRepository;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.SessionService;
import com.sherazasghar.riverside_backend.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Override
    public Session createSession(UUID hostId, SessionCreateRequest request) {
        User user  = userRepository.findById(hostId).orElseThrow(() -> new UserNotFoundException("User with id " + hostId+" not found"));
        Session session = new Session();
        session.setHost(user);
        session.setName(request.getName());
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
    public Session getSessionFromCode(String sessionCode) {
        Session session = sessionRepository.findBySessionCode(sessionCode).orElseThrow(() -> new SessionNotFoundException("Session with code " + sessionCode + " not found"));
        if(session.getStatus() == SessionStatusEnum.COMPLETED || session.getStatus() == SessionStatusEnum.CANCELLED) {
            throw new SessionNotFoundException("Session with code " + sessionCode + " not found");
        }else if(session.getStatus() == SessionStatusEnum.SCHEDULED){
            throw new SessionNotFoundException("Session with code " + sessionCode + " is not active yet");
        }return session;
    }
}
