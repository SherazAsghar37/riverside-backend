package com.sherazasghar.riverside_backend.domain.entities;

import com.sherazasghar.riverside_backend.domain.enums.RecordingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "session_recordings")
@Data
public class SessionRecordings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToMany(mappedBy = "sessionRecordings", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ParticipantRecording> participantRecordings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "is_concluded", nullable = false)
    private Boolean isConcluded=false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
