package com.sherazasghar.riverside_backend.controllers;

import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.dtos.responses.CreateSessionRecordingResponseDto;
import com.sherazasghar.riverside_backend.mappers.SessionRecordingsMapper;
import com.sherazasghar.riverside_backend.services.SessionRecordingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session-recordings")
@RequiredArgsConstructor
public class SessionRecordingController {
    private final SessionRecordingService sessionRecordingService;
    private final SessionRecordingsMapper sessionRecordingsMapper;

    @PostMapping("/start-recording/{sessionCode}")
    public ResponseEntity<CreateSessionRecordingResponseDto> startRecording(
            @AuthenticationPrincipal User user,
            @PathVariable String sessionCode
    ){
        return ResponseEntity.ok(
                sessionRecordingsMapper.fromSessionRecordings(
                        sessionRecordingService.startRecording(user.getId(), sessionCode)
                )
        );
    }

    @PostMapping("/stop-recording/{sessionCode}")
    public ResponseEntity<CreateSessionRecordingResponseDto> stopRecording(
            @AuthenticationPrincipal User user,
            @PathVariable String sessionCode
    ){
        sessionRecordingService.stopRecording(user.getId(), sessionCode);
        return ResponseEntity.noContent().build();
    }
}
