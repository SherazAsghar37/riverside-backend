package com.sherazasghar.riverside_backend.controllers;

import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.dtos.requests.SessionCreateRequestDto;
import com.sherazasghar.riverside_backend.dtos.responses.SessionCreateResponseDto;
import com.sherazasghar.riverside_backend.dtos.responses.SessionDetailsResponseDto;
import com.sherazasghar.riverside_backend.dtos.responses.SessionJoinResponseDto;
import com.sherazasghar.riverside_backend.dtos.responses.SessionsListResponseDto;
import com.sherazasghar.riverside_backend.mappers.SessionMapper;
import com.sherazasghar.riverside_backend.services.SessionService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final SessionMapper sessionMapper;

    @PostMapping("/create-session")
    public ResponseEntity<SessionCreateResponseDto> createSession(
            @Valid @RequestBody SessionCreateRequestDto request,
            @AuthenticationPrincipal User user){
         Session session = sessionService.createSession(user.getId(), sessionMapper.fromDto(request));
         return ResponseEntity.ok(sessionMapper.toDto(session));
    }

    @GetMapping("/get-all-sessions")
    public ResponseEntity<SessionsListResponseDto> getAllSessions(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(
                new SessionsListResponseDto(sessionService.listSessions(user.getId())
                        .stream()
                        .map(sessionMapper::toSessionsListSessionDto)
                        .toList())
        );
    }


    @PostMapping("/join-as-host/{sessionCode}")
    public ResponseEntity<SessionJoinResponseDto> JoinSessionAsHost(
            @PathVariable String sessionCode,
            @AuthenticationPrincipal User user){
        return ResponseEntity.ok(
                sessionMapper.toSessionJoinResponseDto(sessionService.joinSessionAsHost(sessionCode, user.getId())));

    }
    @GetMapping("/information/{sessionCode}")
    public ResponseEntity<SessionDetailsResponseDto> fetchSessionInformation(
            @PathVariable String sessionCode,
            @AuthenticationPrincipal User user,
            @PathParam("issHost") boolean isHost){
        return ResponseEntity.ok(
                sessionMapper.toSessionDetailsResponseDto(
                        isHost? sessionService.joinSessionAsHost(sessionCode, user.getId()):
                        sessionService.sessionFormSessionCode(sessionCode)));

    }
    @PostMapping("/end-session/{sessionCode}")
    public ResponseEntity<SessionDetailsResponseDto> endSession(
            @PathVariable String sessionCode){
        return ResponseEntity.ok(
                sessionMapper.toSessionDetailsResponseDto(sessionService.endSession(sessionCode)));

    }
}
