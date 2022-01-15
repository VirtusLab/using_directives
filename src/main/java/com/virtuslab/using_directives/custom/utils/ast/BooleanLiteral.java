package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.model.UsingDirectiveSyntax;
import com.virtuslab.using_directives.custom.utils.Position;

public class BooleanLiteral extends UsingPrimitive {

  public Boolean getValue() {
    return value;
  }

  private final Boolean value;
  private final UsingDirectiveSyntax syntax;

  public BooleanLiteral(
      Boolean value, Position position, String scope, UsingDirectiveSyntax syntax) {
    super(position, scope);
    this.value = value;
    this.syntax = syntax;
  }

  @Override
  public UsingDirectiveSyntax getSyntax() {
    return syntax;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
