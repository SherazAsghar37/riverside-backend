package com.sherazasghar.riverside_backend.mappers;

import com.sherazasghar.riverside_backend.domain.entities.Session;
import com.sherazasghar.riverside_backend.domain.requests.SessionCreateRequest;
import com.sherazasghar.riverside_backend.dtos.requests.SessionCreateRequestDto;
import com.sherazasghar.riverside_backend.dtos.responses.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE )
public interface SessionMapper {
    SessionCreateRequest fromDto(SessionCreateRequestDto request);

    @Mapping(target = "sessionId", source = "id")
    @Mapping(target = "sessionName", source = "name")
    SessionCreateResponseDto toDto(Session session);

    @Mapping(target = "sessionName", source = "name")
    SessionsListSessionResponseDto toSessionsListSessionDto(Session session);

    @Mapping(target = "sessionId", source = "id")
    SessionJoinResponseDto toSessionJoinResponseDto(Session session);

    @Mapping(target= "hostId", source = "host.id")
    @Mapping(target= "hostName", source = "host.name")
    @Mapping(target = "sessionId", source = "id")
    SessionDetailsResponseDto toSessionDetailsResponseDto(Session session);
}
