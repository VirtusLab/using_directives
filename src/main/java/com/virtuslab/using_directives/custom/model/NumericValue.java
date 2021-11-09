package com.virtuslab.using_directives.custom.model;

import java.util.Objects;

public class NumericValue implements Value<String> {
  private final String v;

  public NumericValue(String v) {
    this.v = v;
  }

  @Override
  public String get() {
    return v;
  }

  @Override
  public String toString() {
    return v;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NumericValue that = (NumericValue) o;
    return Objects.equals(v, that.v);
  }

  @Override
  public int hashCode() {
    return Objects.hash(v);
  }
}
