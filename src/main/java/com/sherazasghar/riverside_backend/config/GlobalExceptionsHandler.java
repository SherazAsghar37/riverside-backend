package com.sherazasghar.riverside_backend.config;

import com.sherazasghar.riverside_backend.dtos.responses.ErrorResponseDto;
import com.sherazasghar.riverside_backend.exceptions.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionsHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation occurred", ex);

        final String message = ex.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessage).orElse("Constraint violation occurred");

        final ErrorResponseDto errorResponseDto = new ErrorResponseDto(message);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Method argument not valid", ex);

        final String message = ex.getBindingResult().getFieldErrors().stream().findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation error occurred");

        final ErrorResponseDto errorResponseDto = new ErrorResponseDto(message);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleException(UserNotFoundException ex) {
        log.error("UserNotFoundException error occurred", ex);
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        log.error("An unexpected error occurred", ex);
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                "An unexpected error occurred. Please try again later.");
        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleException(SessionNotFoundException ex) {
        log.error("SessionNotFoundException error occurred", ex);
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SessionCancelledException.class)
    public ResponseEntity<ErrorResponseDto> handleException(SessionCancelledException ex) {
        log.error("SessionCancelledException error occurred", ex);
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SessionCompletedException.class)
    public ResponseEntity<ErrorResponseDto> handleException(SessionCompletedException ex) {
        log.error("SessionCompletedException error occurred", ex);
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SessionNotStartedException.class)
    public ResponseEntity<ErrorResponseDto> handleException(SessionNotStartedException ex) {
        log.error("SessionNotStartedException error occurred", ex);
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }
}
