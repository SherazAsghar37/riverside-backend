package com.sherazasghar.riverside_backend.domain.entities;

import com.sherazasghar.riverside_backend.domain.enums.RecordingStatus;
import com.sherazasghar.riverside_backend.domain.enums.RecordingType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name= "participant_recording")
@Data
public class ParticipantRecording {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(nullable = false, unique = true)
    private UUID id;

    @JoinColumn(name = "participant_id", nullable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "session_recording_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private SessionRecordings sessionRecordings;

    @Enumerated(EnumType.STRING)
    @Column(name = "recording_status",nullable = false)
    private RecordingStatus recordingStatus;

    @Column(name = "recording_url")
    private String recordingUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RecordingType recordingType ;

    @Column(name = "contains_audio", nullable = false)
    private boolean containsAudio;

    @Column
    private LocalDateTime recordingDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
