package com.sherazasghar.riverside_backend.dtos.responses;

import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionsListSessionResponseDto {
    private String id;
    private String sessionCode;
    private String sessionName;
}
