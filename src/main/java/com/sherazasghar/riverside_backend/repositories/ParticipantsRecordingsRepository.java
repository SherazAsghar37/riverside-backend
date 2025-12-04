package com.sherazasghar.riverside_backend.repositories;

import com.sherazasghar.riverside_backend.domain.entities.ParticipantRecording;
import com.sherazasghar.riverside_backend.domain.enums.RecordingStatus;
import com.sherazasghar.riverside_backend.domain.enums.RecordingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParticipantsRecordingsRepository extends JpaRepository<ParticipantRecording, UUID> {
    List<ParticipantRecording> findByUserIdAndSessionRecordingsIdAndRecordingStatus(UUID userId, UUID recordingId, RecordingStatus recordingStatus);
    Optional<ParticipantRecording> findLastByUserIdAndSessionRecordingsIdAndRecordingStatusAndRecordingType(UUID userId, UUID recordingId, RecordingStatus recordingStatus, RecordingType recordingType);

    List<ParticipantRecording> findBySessionRecordingsIdAndRecordingStatus(UUID sessionRecordingId, RecordingStatus recordingStatus);
}
