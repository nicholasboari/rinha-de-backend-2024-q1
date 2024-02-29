package com.nicholasboari.springproject.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaDuroDormeException.class)
    public ResponseEntity<?> handleTaDuroDormeException(TaDuroDormeException ex) {
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<?> handleClientNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.unprocessableEntity().body(ex.getMessage());
    }
}