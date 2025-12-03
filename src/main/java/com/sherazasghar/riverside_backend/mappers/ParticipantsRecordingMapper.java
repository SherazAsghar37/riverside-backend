package com.sherazasghar.riverside_backend.mappers;

import com.sherazasghar.riverside_backend.domain.entities.ParticipantRecording;
import com.sherazasghar.riverside_backend.dtos.responses.CreateParticipantRecordingResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE )
public interface ParticipantsRecordingMapper {
     CreateParticipantRecordingResponseDto fromParticipantRecording(ParticipantRecording participantRecordingEntity);
}
