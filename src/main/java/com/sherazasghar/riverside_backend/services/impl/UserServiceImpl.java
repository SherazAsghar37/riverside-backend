package com.sherazasghar.riverside_backend.services.impl;

import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.domain.requests.UserCreateRequest;
import com.sherazasghar.riverside_backend.domain.requests.UserLoginRequest;
import com.sherazasghar.riverside_backend.dtos.requests.UserCreateRequestDto;
import com.sherazasghar.riverside_backend.exceptions.UserNotFoundException;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void createUser(UserCreateRequest userCreateRequestDto) {
            User user = new User();
            user.setName(userCreateRequestDto.getName());
            user.setEmail(userCreateRequestDto.getEmail());
            user.setPassword(passwordEncoder.encode(userCreateRequestDto.getPassword()));
             userRepository.save(user);
    }

    @Override
    public User login(UserLoginRequest userLoginRequestDto) {
        return userRepository.findByEmail(userLoginRequestDto.getEmail()).orElseThrow(
                ()-> new UserNotFoundException("There is no user with this email"));
    }
}
