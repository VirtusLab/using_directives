package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;

public abstract class UsingPrimitive extends UsingValue {
  private String scope;

  public UsingPrimitive(Position position) {
    super(position);
    this.scope = null;
  }

  public UsingPrimitive(Position position, String scope) {
    super(position);
    this.scope = scope;
  }

  public UsingPrimitive() {
    this.scope = null;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }
}
