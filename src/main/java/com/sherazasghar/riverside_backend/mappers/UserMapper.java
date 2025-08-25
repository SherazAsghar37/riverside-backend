package com.sherazasghar.riverside_backend.mappers;


import com.sherazasghar.riverside_backend.domain.requests.UserCreateRequest;
import com.sherazasghar.riverside_backend.domain.requests.UserLoginRequest;
import com.sherazasghar.riverside_backend.dtos.requests.UserCreateRequestDto;
import com.sherazasghar.riverside_backend.dtos.requests.UserLoginRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE )
@Component
public interface UserMapper {
    UserCreateRequest toUserCreateRequest(UserCreateRequestDto userCreateRequestDto) ;
    UserLoginRequest toUserLoginRequest(UserLoginRequestDto userLoginRequestDto);
}
