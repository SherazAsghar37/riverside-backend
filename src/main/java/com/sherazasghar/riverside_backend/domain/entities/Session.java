package com.sherazasghar.riverside_backend.domain.entities;

import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionStatusEnum status= SessionStatusEnum.ONGOING;

    @Column(name = "session_code", nullable = false, unique = true)
    private String sessionCode;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SessionParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SessionRecordings> sessionRecordings = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
