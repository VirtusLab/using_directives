package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import java.util.Objects;

public class EmptyValue extends Value<String> {

  public EmptyValue(UsingTree astNode) {
    super(astNode);
  }

  public EmptyValue(UsingTree astNode, String scope) {
    super(astNode, scope);
  }

  @Override
  public String get() {
    return "<EmptyValue>";
  }

  @Override
  public String toString() {
    return "<EmptyValue>";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EmptyValue that = (EmptyValue) o;
    return Objects.equals(getScope(), that.getScope());
  }

  @Override
  public int hashCode() {
    return Objects.hash("<EmptyValue>");
  }
}
