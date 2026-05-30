package com.anouar.elearning.analytics.exception;

import com.anouar.elearning.analytics.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<ApiResponse<Void>> unauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
    }
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiResponse<Void>> notFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<Void>builder().success(false).message(ex.getMessage()).build());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> badRequest() {
        return ResponseEntity.badRequest().body(ApiResponse.<Void>builder().success(false).message("Validation error").build());
    }
}
