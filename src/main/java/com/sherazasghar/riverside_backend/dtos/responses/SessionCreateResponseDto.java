package com.sherazasghar.riverside_backend.dtos.responses;

import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateResponseDto {
    private String sessionId;
    private String sessionName;
    private LocalDateTime scheduledAt;
    private SessionStatusEnum status;
    private String sessionCode;
}
