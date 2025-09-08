package com.sherazasghar.riverside_backend.dtos.responses;

import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionsListResponseDto {
    private List<SessionsListSessionResponseDto> sessions;
}
