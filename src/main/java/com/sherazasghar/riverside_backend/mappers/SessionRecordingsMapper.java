package com.sherazasghar.riverside_backend.mappers;

import com.sherazasghar.riverside_backend.domain.entities.SessionRecordings;
import com.sherazasghar.riverside_backend.dtos.responses.CreateSessionRecordingResponseDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring",unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE )
@Component
public interface SessionRecordingsMapper {
    CreateSessionRecordingResponseDto fromSessionRecordings(SessionRecordings sessionRecordings);
}
