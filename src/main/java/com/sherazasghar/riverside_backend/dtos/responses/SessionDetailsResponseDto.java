package com.sherazasghar.riverside_backend.dtos.responses;

import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDetailsResponseDto {
    private UUID sessionId;
    private String name;
    private SessionStatusEnum status;
    private String sessionCode;
    private LocalDateTime scheduledAt;
    private UUID hostId;
    private String hostName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
