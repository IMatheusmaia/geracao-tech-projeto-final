package com.agent.agentApi.rest.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
      "timestamp", LocalDateTime.now(),
      "status", 404,
      "error", "Not Found",
      "message", ex.getMessage()
    ));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
      .map(err -> Map.of("field", err.getField(), "message", err.getDefaultMessage()))
      .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
      "timestamp", LocalDateTime.now(),
      "status", 400,
      "error", "Bad Request",
      "errors", errors
    ));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
      "timestamp", LocalDateTime.now(),
      "status", 401,
      "error", "Unauthorized",
      "message", "Credenciais inválidas"
    ));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
      "timestamp", LocalDateTime.now(),
      "status", 403,
      "error", "Forbidden",
      "message", "Acesso negado"
    ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
      "timestamp", LocalDateTime.now(),
      "status", 500,
      "error", "Internal Server Error",
      "message", ex.getMessage()
    ));
  }
}
