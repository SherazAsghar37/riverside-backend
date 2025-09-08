package com.sherazasghar.riverside_backend.services;


import com.sherazasghar.riverside_backend.domain.entities.SessionParticipant;

import java.util.UUID;

public interface SessionParticipantService {
    SessionParticipant addParticipantToSession(UUID sessionId, UUID userId);
    void removeParticipantFromSession(UUID sessionId, UUID userId);
}
