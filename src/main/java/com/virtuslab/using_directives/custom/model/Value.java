package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;

public abstract class Value<T> implements ValueOrSetting<T> {
  private final UsingTree astNode;
  private final String scope;
  private final UsingDirectiveSyntax syntax;

  public Value(UsingTree astNode, String scope, UsingDirectiveSyntax syntax) {
    this.astNode = astNode;
    this.scope = scope;
    this.syntax = syntax;
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

  public UsingDirectiveSyntax getSyntax() {
    return syntax;
  }
}
