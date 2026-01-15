package com.kml.capacity.dto;

public class QuantityUpdateDto {

  private int delta;

  public QuantityUpdateDto() {}

  public QuantityUpdateDto(int delta) {
    this.delta = delta;
  }

  public int getDelta() {
    return delta;
  }

  public void setDelta(int delta) {
    this.delta = delta;
  }
}
