package com.virtuslab.using_directives.custom.model;

import java.util.Objects;

public class BooleanValue implements Value<Boolean> {
  private final Boolean v;

  public BooleanValue(Boolean v) {
    this.v = v;
  }

  @Override
  public Boolean get() {
    return v;
  }

  @Override
  public String toString() {
    return v.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BooleanValue that = (BooleanValue) o;
    return Objects.equals(v, that.v);
  }

  @Override
  public int hashCode() {
    return Objects.hash(v);
  }
}
