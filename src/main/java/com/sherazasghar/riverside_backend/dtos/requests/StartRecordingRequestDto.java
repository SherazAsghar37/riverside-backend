package com.sherazasghar.riverside_backend.dtos.requests;

import com.sherazasghar.riverside_backend.domain.enums.RecordingType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartRecordingRequestDto {
    @NotBlank(message = "Session Recording ID is required")
    private String sessionRecordingId;

    @NotNull(message = "Contains Audio field is required")
    private boolean containsAudio;

    @NotNull(message = "Contains Video field is required")
    private RecordingType recordingType;
}
