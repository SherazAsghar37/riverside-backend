package com.sherazasghar.riverside_backend.controllers;

import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.dtos.requests.StartRecordingRequestDto;
import com.sherazasghar.riverside_backend.dtos.responses.CreateParticipantRecordingResponseDto;
import com.sherazasghar.riverside_backend.mappers.ParticipantsRecordingMapper;
import com.sherazasghar.riverside_backend.services.ParticipantsRecordingService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/participant-recordings")
@RequiredArgsConstructor
public class ParticipantRecordingController {
    private final ParticipantsRecordingMapper participantsRecordingMapper;
    private final ParticipantsRecordingService participantsRecordingService;

    @PostMapping("/start-recording")
     public ResponseEntity<CreateParticipantRecordingResponseDto> startRecording(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StartRecordingRequestDto startRecordingRequestDto
    ){
        return  ResponseEntity.ok(
                participantsRecordingMapper.fromParticipantRecording(
                        participantsRecordingService.startRecording(user.getId(), startRecordingRequestDto)
                )
        );
    }
    @PostMapping("/stop-recording/{sessionRecordingId}")
     public ResponseEntity<Void> stopRecording(
            @AuthenticationPrincipal User user,
             @PathVariable String sessionRecordingId
    ){
        participantsRecordingService.stopRecording(user.getId(), UUID.fromString(sessionRecordingId));
        return  ResponseEntity.noContent().build();

    }

}
