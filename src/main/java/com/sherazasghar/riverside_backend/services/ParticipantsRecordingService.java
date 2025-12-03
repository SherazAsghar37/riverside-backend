package com.sherazasghar.riverside_backend.services;


import com.sherazasghar.riverside_backend.domain.entities.ParticipantRecording;
import com.sherazasghar.riverside_backend.dtos.requests.StartRecordingRequestDto;

import java.util.UUID;

public interface ParticipantsRecordingService {
    ParticipantRecording startRecording(UUID participantId, StartRecordingRequestDto requestDto);
    void stopRecording(UUID participantId, UUID sessionRecordingId);

}
