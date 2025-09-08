package com.sherazasghar.riverside_backend.repositories;

import com.sherazasghar.riverside_backend.domain.entities.SessionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, UUID> {
}
