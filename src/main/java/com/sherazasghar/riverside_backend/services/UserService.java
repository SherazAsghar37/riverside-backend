package com.sherazasghar.riverside_backend.services;


import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.requests.UserCreateRequest;
import com.sherazasghar.riverside_backend.domain.requests.UserLoginRequest;
import com.sherazasghar.riverside_backend.dtos.requests.UserCreateRequestDto;

public interface UserService {
    void createUser(UserCreateRequest userCreateRequestDto);
    User login(UserLoginRequest userLoginRequestDto);
}
