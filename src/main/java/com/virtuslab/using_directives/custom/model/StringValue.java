package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import java.util.Objects;

public class StringValue extends Value<String> {
  private final String v;

  public StringValue(String v, UsingTree astNode, UsingDirectiveSyntax syntax, String scope) {
    super(astNode, scope, syntax);
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
    StringValue that = (StringValue) o;
    return Objects.equals(v, that.v) && Objects.equals(getScope(), that.getScope());
  }

  @Override
  public int hashCode() {
    return Objects.hash(v);
  }
}
