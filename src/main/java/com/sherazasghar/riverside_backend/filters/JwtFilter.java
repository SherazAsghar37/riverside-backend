package com.sherazasghar.riverside_backend.filters;

import com.sherazasghar.riverside_backend.domain.entities.User;
import com.sherazasghar.riverside_backend.repositories.UserRepository;
import com.sherazasghar.riverside_backend.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    final JwtService jwtService;
    final UserRepository userRepository;

    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       try{
           final String authorizationHeader = request.getHeader("Authorization");
           final String token = authorizationHeader == null ? null : authorizationHeader.substring("Bearer ".length());
           if(token == null){
               filterChain.doFilter(request, response);
               return;
           }
           final String id = jwtService.parseTokenAndGetUserId(token);
           final User user = userRepository.findById(UUID.fromString(id)).orElse(null);

           if(user!=null && SecurityContextHolder.getContext().getAuthentication()==null){
               Authentication authentication =new UsernamePasswordAuthenticationToken(user,null);
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
           filterChain.doFilter(request, response);
       }catch(Exception e){
           exceptionResolver.resolveException(request, response, null, e);
       }

    }
}
