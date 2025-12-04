package com.sherazasghar.riverside_backend.dtos.requests;

import com.sherazasghar.riverside_backend.domain.enums.RecordingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StopSpecificRecordingRequestDto {
    @NotBlank(message = "Session Recording ID is required")
    private String sessionRecordingId;
    @NotNull(message = "Recording Type field is required")
    private RecordingType recordingType;
}
