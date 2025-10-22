package com.sherazasghar.riverside_backend.dtos.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDto {
    private UUID id;
    private String name;
    private String email;
    private String token;
}
