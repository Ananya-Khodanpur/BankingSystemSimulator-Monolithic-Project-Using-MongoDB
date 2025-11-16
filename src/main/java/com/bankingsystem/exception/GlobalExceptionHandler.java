package com.bankingsystem.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to avoid repeating same Map everywhere
    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                ));
    }

    // -------------------- Custom Exceptions --------------------

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> handleAccountNotFound(AccountNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<?> handleInvalidAmount(InvalidAmountException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<?> handleInsufficientBalance(InsufficientBalanceException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
