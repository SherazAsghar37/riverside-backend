package com.sherazasghar.riverside_backend.services.impl;

import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.entities.SessionParticipant;
import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.enums.ParticipantEventType;
import com.sherazasghar.riverside_backend.exceptions.SessionNotFoundException;
import com.sherazasghar.riverside_backend.exceptions.UserNotFoundException;
import com.sherazasghar.riverside_backend.repositories.SessionParticipantRepository;
import com.sherazasghar.riverside_backend.repositories.SessionRepository;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.SessionParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionParticipantServiceImpl implements SessionParticipantService {
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final SessionParticipantRepository sessionParticipantRepository;

    @Override
    public SessionParticipant addParticipantToSession(UUID sessionId, UUID userId) {
        SessionParticipant sessionParticipant =  createParticipantEntry(sessionId, userId);
        sessionParticipant.setEventType(ParticipantEventType.JOINED);

        return sessionParticipantRepository.save(sessionParticipant);
    }

    @Override
    public void removeParticipantFromSession(UUID sessionId, UUID userId) {
        SessionParticipant sessionParticipant =  createParticipantEntry(sessionId, userId);
        sessionParticipant.setEventType(ParticipantEventType.LEFT);

        sessionParticipantRepository.save(sessionParticipant);
    }

    private SessionParticipant createParticipantEntry(UUID sessionId, UUID userId) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new SessionNotFoundException("Session not found! ID: " + sessionId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found! ID: " + userId));

        SessionParticipant sessionParticipant = new SessionParticipant();
        sessionParticipant.setSession(session);
        sessionParticipant.setUser(user);

        return sessionParticipant;
    }
}
