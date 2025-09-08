package com.sherazasghar.riverside_backend.domain.entities;

import com.sherazasghar.riverside_backend.domain.enums.RecordingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "session_recordings")
@Data
public class SessionRecordings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "recording_url", nullable = false)
    private String recordingUrl;

    @Column(name = "uploaded_at", nullable = false)
    private String filename;

    @Column(name = "status", nullable = false)
    private RecordingStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
