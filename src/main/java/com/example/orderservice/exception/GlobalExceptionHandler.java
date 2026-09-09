package com.example.orderservice.exception;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleFieldValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        log.warn("Field validation failed: {}", fieldErrors);
        return buildError(HttpStatus.BAD_REQUEST, "Field validation failed", fieldErrors);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessValidation(ValidationException ex) {
        log.warn("Business validation failed: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(OrderNotFoundException ex) {
        log.warn("Order not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(PartnerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePartnerNotFound(PartnerNotFoundException ex) {
        log.error("Partner not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(GitHubUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleGitHubUnavailable(GitHubUnavailableException ex) {
        log.error("GitHub API unavailable: {}", ex.getMessage());
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "GitHub API is temporarily unavailable", null);
    }

    @ExceptionHandler(InvalidGitHubLinkException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidGitHubLink(InvalidGitHubLinkException ex) {
        log.warn("Invalid GitHub link: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Map<String, Object>> handleCircuitOpen(CallNotPermittedException ex) {
        log.warn("Circuit breaker '{}' is OPEN, request rejected fast", ex.getCausingCircuitBreakerName());
        return buildError(
                HttpStatus.SERVICE_UNAVAILABLE, "External service is temporarily unavailable, try again later", null);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RequestNotPermitted ex) {
        log.warn("Rate limiter rejected request: {}", ex.getMessage());
        return buildError(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded, try again later", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Parameter '" + ex.getName() + "' has invalid value: " + ex.getValue();
        log.warn("Method argument type mismatch: {}", message);
        return buildError(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message, Object details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (details != null) {
            body.put("details", details);
        }
        return ResponseEntity.status(status).body(body);
    }
}
