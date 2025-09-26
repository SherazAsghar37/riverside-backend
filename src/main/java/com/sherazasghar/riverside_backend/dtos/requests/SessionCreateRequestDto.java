package com.sherazasghar.riverside_backend.dtos.requests;

import com.sherazasghar.riverside_backend.domain.enums.SessionStatusEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequestDto {
    @Nullable
    @FutureOrPresent(message = "Scheduled time must be in the present or future")
    private LocalDateTime scheduledAt;

    private SessionStatusEnum status;
}
