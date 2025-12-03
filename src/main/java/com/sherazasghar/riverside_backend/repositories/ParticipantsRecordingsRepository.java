package com.sherazasghar.riverside_backend.repositories;

import com.sherazasghar.riverside_backend.domain.entities.ParticipantRecording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantsRecordingsRepository extends JpaRepository<ParticipantRecording, UUID> {
    Optional<ParticipantRecording> findByUserIdAndSessionRecordingsId(UUID userId, UUID recordingId);
}
