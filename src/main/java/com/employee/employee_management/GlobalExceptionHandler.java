package com.employee.employee_management;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

@ExceptionHandler(ResponseStatusException.class)
public ResponseEntity<Map<String, Object>> handleResponseStatusException(
        ResponseStatusException ex) {

    Map<String, Object> error = new HashMap<>();

    error.put("status", ex.getStatusCode().value());
    error.put("message", ex.getReason());

    return new ResponseEntity<>(error, ex.getStatusCode());
}

}

