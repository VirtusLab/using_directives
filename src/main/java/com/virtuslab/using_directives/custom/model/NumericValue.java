package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import java.util.Objects;

public class NumericValue extends Value<String> {
  private final String v;

  public NumericValue(String v, UsingTree astNode) {
    super(astNode);
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
