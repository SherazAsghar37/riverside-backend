package com.sherazasghar.riverside_backend.services;

import com.sherazasghar.riverside_backend.domain.entities.User;

import javax.crypto.SecretKey;

public interface JwtService {
     String generateJwtToken(User user) ;

     SecretKey getSigningKey();

     String parseTokenAndGetUserId(String token);
}
