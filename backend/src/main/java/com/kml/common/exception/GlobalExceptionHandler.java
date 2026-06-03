package com.kml.common.exception;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/** Centralized exception handling for API. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 400 Bad Request
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiException> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {

    ApiException error =
        new ApiException(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  // 409 Conflict - Optimistic locking failure
  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ApiException> handleOptimisticLocking(
      ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {

    ApiException error =
        new ApiException(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            "The resource was modified by another request. Please retry.",
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  // 409 Conflict
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiException> handleIllegalState(
      IllegalStateException ex, HttpServletRequest request) {

    ApiException error =
        new ApiException(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  // 400 Bad Request - Validation errors
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiException> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + " " + e.getDefaultMessage())
            .reduce((msg1, msg2) -> msg1 + "; " + msg2)
            .orElse("Validation error");

    ApiException error =
        new ApiException(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            message,
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  // 404 Not Found
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiException> handleNotFound(
      NoSuchElementException ex, HttpServletRequest request) {

    ApiException error =
        new ApiException(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  // 403 Forbidden - Access denied (Spring Security)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiException> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {

    ApiException error =
        new ApiException(
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  // 403 Forbidden - Ownership violation
  @ExceptionHandler(OwnershipException.class)
  public ResponseEntity<ApiException> handleOwnershipViolation(
      OwnershipException ex, HttpServletRequest request) {

    ApiException error =
        new ApiException(
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            ex.getMessage() != null ? ex.getMessage() : "User does not have ownership",
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  // 500 Internal Server Error
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiException> handleGenericException(Exception ex, HttpServletRequest request) {

    ApiException error =
        new ApiException(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Unexpected server error",
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
