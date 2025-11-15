package org.fixmed.fixmed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> błędy = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            błędy.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(błędy);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("błąd", ex.getMessage()));
    }

    // Obsługa błędnych danych wejściowych (np. niepoprawna płeć)
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Map<String, String>> handleInvalid(InvalidInputException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("błąd", ex.getMessage()));
    }
}
