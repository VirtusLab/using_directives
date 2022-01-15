package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.model.UsingDirectiveSyntax;
import com.virtuslab.using_directives.custom.utils.Position;
import java.util.List;

public class UsingValues extends UsingValue {
  private final List<UsingPrimitive> values;
  private final UsingDirectiveSyntax syntax;

  public UsingValues(List<UsingPrimitive> values, Position position, UsingDirectiveSyntax syntax) {
    super(position);
    this.values = values;
    this.syntax = syntax;
  }

  public List<UsingPrimitive> getValues() {
    return values;
  }

  @Override
  public UsingDirectiveSyntax getSyntax() {
    return syntax;
  }

  @Override
  public String toString() {
    return "UsingValues(" + values + ')';
  }
}
