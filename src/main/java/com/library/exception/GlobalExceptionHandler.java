package com.library.exception;

import com.library.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyRegistered(
            EmailAlreadyRegisteredException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, exception.getMessage(), null));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFound(
            EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, exception.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(
            IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, exception.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalState(
            IllegalStateException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, exception.getMessage(), null));
    }
}
