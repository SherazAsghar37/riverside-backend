package com.sherazasghar.riverside_backend.services.impl;

import com.sherazasghar.riverside_backend.domain.entities.ParticipantRecording;
import com.sherazasghar.riverside_backend.domain.entities.SessionRecordings;
import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.enums.RecordingStatus;
import com.sherazasghar.riverside_backend.dtos.requests.StartRecordingRequestDto;
import com.sherazasghar.riverside_backend.exceptions.ParticipantsRecordingNotFoundException;
import com.sherazasghar.riverside_backend.exceptions.SessionRecordingNotFoundException;
import com.sherazasghar.riverside_backend.exceptions.UserNotFoundException;
import com.sherazasghar.riverside_backend.repositories.ParticipantsRecordingsRepository;
import com.sherazasghar.riverside_backend.repositories.SessionRecordingsRepository;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.ParticipantsRecordingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantsRecordingServiceImpl implements ParticipantsRecordingService {
    private final ParticipantsRecordingsRepository participantsRecordingsRepository;
    private final UserRepository userRepository;
    private final SessionRecordingsRepository sessionRecordingsRepository;
    @Override
    public ParticipantRecording startRecording(UUID participantId, StartRecordingRequestDto requestDto) {
        User user = userRepository.findById(participantId).orElseThrow(
        ()-> new UserNotFoundException("User with id "+participantId+" not found"));

        SessionRecordings sessionRecordings = sessionRecordingsRepository.findById(UUID.fromString(requestDto.getSessionRecordingId())).orElseThrow(
                ()-> new SessionRecordingNotFoundException("Session Recording with id "+requestDto.getSessionRecordingId()+" not found")
        );

        ParticipantRecording participantRecording = new ParticipantRecording();
        participantRecording.setUser(user);
        participantRecording.setSessionRecordings(sessionRecordings);
        participantRecording.setRecordingStatus(RecordingStatus.RECORDING);
        participantRecording.setRecordingType(requestDto.getRecordingType());
        participantRecording.setContainsAudio(requestDto.isContainsAudio());

        return participantsRecordingsRepository.save(participantRecording);
    }

    @Override
    public void stopRecording(UUID participantId, UUID sessionRecordingId) {
        ParticipantRecording participantRecording = participantsRecordingsRepository.findByUserIdAndSessionRecordingsId(participantId,sessionRecordingId).orElse(null);
        if(participantRecording==null) return;
        participantRecording.setRecordingStatus(RecordingStatus.MERGING);
        participantsRecordingsRepository.save(participantRecording);
    }
}
