package com.kml.common.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kml.security.TooManyRequestsException;

import jakarta.servlet.http.HttpServletRequest;

/** Centralized exception handling for API. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiException> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error(HttpStatus.BAD_REQUEST, safeMessage(ex.getMessage(), "Invalid request"), request));
  }

  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ApiException> handleOptimisticLocking(
      ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(error(HttpStatus.CONFLICT, "The resource was modified by another request. Please retry.", request));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiException> handleIllegalState(
      IllegalStateException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(error(HttpStatus.CONFLICT, safeMessage(ex.getMessage(), "Operation could not be completed"), request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiException> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(this::formatFieldError)
        .reduce((left, right) -> left + "; " + right)
        .orElse("Validation error");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error(HttpStatus.BAD_REQUEST, message, request));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiException> handleNotFound(
      NoSuchElementException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(error(HttpStatus.NOT_FOUND, safeMessage(ex.getMessage(), "Resource not found"), request));
  }



  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiException> handleNoHandlerFound(
      NoHandlerFoundException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(error(HttpStatus.NOT_FOUND, "Resource not found", request));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiException> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(error(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", request));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiException> handleAuthenticationFailure(
      AuthenticationException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(error(HttpStatus.UNAUTHORIZED, "Invalid username or password", request));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiException> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(error(HttpStatus.FORBIDDEN, "Access denied", request));
  }

  @ExceptionHandler(OwnershipException.class)
  public ResponseEntity<ApiException> handleOwnershipViolation(
      OwnershipException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(error(HttpStatus.FORBIDDEN, safeMessage(ex.getMessage(), "User does not have ownership"), request));
  }

  @ExceptionHandler(TooManyRequestsException.class)
  public ResponseEntity<ApiException> handleTooManyRequests(
      TooManyRequestsException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(error(HttpStatus.TOO_MANY_REQUESTS, safeMessage(ex.getMessage(), "Too many requests"), request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiException> handleGenericException(Exception ex, HttpServletRequest request) {
    log.error("Unhandled exception for {} {}", request.getMethod(), request.getRequestURI(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request));
  }

  private ApiException error(HttpStatus status, String message, HttpServletRequest request) {
    return new ApiException(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
  }

  private String formatFieldError(FieldError fieldError) {
    return fieldError.getField() + " " + safeMessage(fieldError.getDefaultMessage(), "is invalid");
  }

  private String safeMessage(String message, String fallback) {
    return message == null || message.isBlank() ? fallback : message;
  }
}
