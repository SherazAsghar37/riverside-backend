package com.sherazasghar.riverside_backend.services;


import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.requests.SessionCreateRequest;

import java.util.List;
import java.util.UUID;

public interface SessionService {
    Session createSession(UUID hostId, SessionCreateRequest request);

    List<Session> listSessions(UUID hostId);

    Session sessionFormSessionCode(String sessionCode);

    Session joinSessionAsHost(String sessionCode, UUID hostId);

    Session endSession(String sessionCode);
}
