package com.kml.api.error;

/**
 * Exception thrown when a user tries to access or modify an entity they do not own. Used to
 * centralize ownership enforcement.
 */
public class OwnershipException extends RuntimeException {

  public OwnershipException(String message) {
    super(message);
  }
}
