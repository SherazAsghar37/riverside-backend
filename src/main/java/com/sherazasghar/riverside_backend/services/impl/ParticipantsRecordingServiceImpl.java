package com.sherazasghar.riverside_backend.services.impl;

import com.sherazasghar.riverside_backend.domain.entities.ParticipantRecording;
import com.sherazasghar.riverside_backend.domain.entities.SessionRecordings;
import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.enums.RecordingStatus;
import com.sherazasghar.riverside_backend.domain.enums.RecordingType;
import com.sherazasghar.riverside_backend.dtos.requests.StartRecordingRequestDto;
import com.sherazasghar.riverside_backend.dtos.requests.StopSpecificRecordingRequestDto;
import com.sherazasghar.riverside_backend.exceptions.ParticipantsRecordingNotFoundException;
import com.sherazasghar.riverside_backend.exceptions.SessionRecordingNotFoundException;
import com.sherazasghar.riverside_backend.exceptions.UserNotFoundException;
import com.sherazasghar.riverside_backend.repositories.ParticipantsRecordingsRepository;
import com.sherazasghar.riverside_backend.repositories.SessionRecordingsRepository;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.ParticipantsRecordingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

        if(requestDto.getRecordingType().equals(RecordingType.BOTH)){
            ParticipantRecording   participantCameraRecording =   createParticipantRecording(user, sessionRecordings, requestDto, RecordingType.CAMERA);
            ParticipantRecording participantScreenRecording =  createParticipantRecording(user, sessionRecordings, requestDto, RecordingType.SCREEN);

            participantsRecordingsRepository.saveAll(List.of( participantCameraRecording, participantScreenRecording));
            return participantCameraRecording;
        }else if(requestDto.getRecordingType().equals(RecordingType.CAMERA)){
            ParticipantRecording participantRecording =  createParticipantRecording(user, sessionRecordings, requestDto, RecordingType.CAMERA);
            return participantsRecordingsRepository.save(participantRecording);
        }else {
            ParticipantRecording participantRecording = createParticipantRecording(user, sessionRecordings, requestDto, RecordingType.SCREEN);
            return participantsRecordingsRepository.save(participantRecording);
        }

    }

    @Override
    public void stopRecording(UUID participantId, UUID sessionRecordingId) {
        List<ParticipantRecording> participantRecordings = participantsRecordingsRepository.findByUserIdAndSessionRecordingsIdAndRecordingStatus(participantId,sessionRecordingId, RecordingStatus.RECORDING);
        if(participantRecordings.isEmpty()) return;
        for (ParticipantRecording pr  :participantRecordings){
            pr.setRecordingStatus(RecordingStatus.MERGING);
        }
        participantsRecordingsRepository.saveAll(participantRecordings);
    }

    @Override
    public void stopSpecificRecording(UUID participantId, StopSpecificRecordingRequestDto requestDto) {
        ParticipantRecording participantRecording = participantsRecordingsRepository.findLastByUserIdAndSessionRecordingsIdAndRecordingStatusAndRecordingType(
                participantId,
                UUID.fromString(requestDto.getSessionRecordingId()),
                RecordingStatus.RECORDING,
                requestDto.getRecordingType()
        ).orElseThrow(
                ()-> new ParticipantsRecordingNotFoundException("Participant recording not found for participant id "+participantId+" and session recording id "+requestDto.getSessionRecordingId())
        );
        participantRecording.setRecordingStatus(RecordingStatus.MERGING);
        participantsRecordingsRepository.save(participantRecording);
    }

    @Override
    public ParticipantRecording startSpecificRecording(UUID participantId, StartRecordingRequestDto requestDto) {
        User user = userRepository.findById(participantId).orElseThrow(
                ()-> new UserNotFoundException("User with id "+participantId+" not found"));

        SessionRecordings sessionRecordings = sessionRecordingsRepository.findById(UUID.fromString(requestDto.getSessionRecordingId())).orElseThrow(
                ()-> new SessionRecordingNotFoundException("Session Recording with id "+requestDto.getSessionRecordingId()+" not found")
        );

         if(requestDto.getRecordingType().equals(RecordingType.CAMERA)){
            ParticipantRecording participantRecording =  createParticipantRecording(user, sessionRecordings, requestDto, RecordingType.CAMERA);
            return participantsRecordingsRepository.save(participantRecording);
        }else if(requestDto.getRecordingType().equals(RecordingType.SCREEN)) {
            ParticipantRecording participantRecording = createParticipantRecording(user, sessionRecordings, requestDto, RecordingType.SCREEN);
            return participantsRecordingsRepository.save(participantRecording);
        }
        throw new IllegalArgumentException("Invalid recording type for specific recording");
    }

    @Override
    public void stopAllRecordingsBySessionRecordingId(UUID sessionRecordingId) {
        List<ParticipantRecording> participantRecordings = participantsRecordingsRepository.findBySessionRecordingsIdAndRecordingStatus(sessionRecordingId, RecordingStatus.RECORDING);

        for(ParticipantRecording pr  :participantRecordings){
            pr.setRecordingStatus(RecordingStatus.MERGING);
        }
        participantsRecordingsRepository.saveAll(participantRecordings);
    }

    ParticipantRecording createParticipantRecording(User user, SessionRecordings sessionRecordings, StartRecordingRequestDto requestDto, RecordingType recordingType) {
        ParticipantRecording participantRecording = new ParticipantRecording();
        participantRecording.setUser(user);
        participantRecording.setSessionRecordings(sessionRecordings);
        participantRecording.setRecordingStatus(RecordingStatus.RECORDING);
        participantRecording.setRecordingType(recordingType);
        participantRecording.setContainsAudio(requestDto.isContainsAudio());
        return participantsRecordingsRepository.save(participantRecording);
    }
}
