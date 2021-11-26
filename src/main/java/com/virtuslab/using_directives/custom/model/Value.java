package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;

public abstract class Value<T> implements ValueOrSetting<T> {
  private final UsingTree astNode;
  private final String scope;

  public Value(UsingTree astNode) {
    this.astNode = astNode;
    this.scope = null;
  }

  public Value(UsingTree astNode, String scope) {
    this.astNode = astNode;
    this.scope = scope;
  }

  @Override
  public UsingTree getRelatedASTNode() {
    return astNode;
  }

  public String getScope() {
    return scope;
  }

  @Override
  public String toString() {
    return get().toString();
  }
}
