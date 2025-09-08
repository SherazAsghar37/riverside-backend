package com.sherazasghar.riverside_backend.domain.entities;

import com.sherazasghar.riverside_backend.domain.enums.ParticipantEventType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "session_participants")
@Data
public class SessionParticipant {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO, generator = "UUID")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, updatable = false)
    private Session session;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ParticipantEventType eventType;

    @CreatedDate
    private LocalDateTime timestamp;
}
