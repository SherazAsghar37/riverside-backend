package com.sherazasghar.riverside_backend.domain.requests;

import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequest {
    private LocalDateTime scheduledAt;
    private SessionStatusEnum status;
}
