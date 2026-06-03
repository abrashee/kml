package com.kml.common.idempotency;

public class CachedResponse {

  private final int status;
  private final byte[] body;
  private final String contentType;
  private final long createdAt;

  public CachedResponse(int status, byte[] body, String contentType) {
    this.status = status;
    this.body = body;
    this.contentType = contentType;
    this.createdAt = System.currentTimeMillis();
  }

  public int getStatus() {
    return status;
  }

  public byte[] getBody() {
    return body;
  }

  public String getContentType() {
    return contentType;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public boolean isExpired(long ttlMs) {
    return System.currentTimeMillis() - createdAt > ttlMs;
  }
}
