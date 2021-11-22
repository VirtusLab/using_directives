package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;

public abstract class Value<T> implements ValueOrSetting<T> {
  private final UsingTree astNode;

  public Value(UsingTree astNode) {
    this.astNode = astNode;
  }

  @Override
  public UsingTree getRelatedASTNode() {
    return astNode;
  }

  @Override
  public String toString() {
    return get().toString();
  }
}
