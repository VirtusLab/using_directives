package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.model.UsingDirectiveSyntax;
import com.virtuslab.using_directives.custom.utils.Position;

public class StringLiteral extends UsingPrimitive {
  public String getValue() {
    return value;
  }

  private final String value;
  private final UsingDirectiveSyntax syntax;

  public StringLiteral(String value, Position position, String scope, UsingDirectiveSyntax syntax) {
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
    return "\"" + value + "\"";
  }
}
