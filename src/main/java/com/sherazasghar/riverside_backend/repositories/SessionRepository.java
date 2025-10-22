package com.sherazasghar.riverside_backend.repositories;

import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByHostId(UUID hostId);

    Optional<Session> findBySessionCodeAndHostId(String sessionCode, UUID hostId);

    Optional<Session> findBySessionCode(String sessionCode);

    Optional<Session> findById(UUID id);

}
