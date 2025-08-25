package com.sherazasghar.riverside_backend.controllers;

import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.dtos.requests.UserCreateRequestDto;
import com.sherazasghar.riverside_backend.dtos.requests.UserLoginRequestDto;
import com.sherazasghar.riverside_backend.dtos.responses.UserCreateResponseDto;
import com.sherazasghar.riverside_backend.dtos.responses.UserLoginResponseDto;
import com.sherazasghar.riverside_backend.mappers.UserMapper;
import com.sherazasghar.riverside_backend.services.UserService;
import com.sherazasghar.riverside_backend.services.impl.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final  UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponseDto> Signup(
            @Valid @RequestBody UserCreateRequestDto userCreateRequestDto){
        userService.createUser(userMapper.toUserCreateRequest(userCreateRequestDto));
        return new ResponseEntity<>(new UserCreateResponseDto("User created successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> SignIn(
            @Valid @RequestBody UserLoginRequestDto userLoginRequestDto){
        final User user = userService.login(userMapper.toUserLoginRequest(userLoginRequestDto));
        final String token = jwtService.generateJwtToken(user);
        return new ResponseEntity<>(new UserLoginResponseDto(user.getName(),user.getEmail(),token), HttpStatus.OK);
    }
}
