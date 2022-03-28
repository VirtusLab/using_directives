package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;

public class EmptyLiteral extends UsingPrimitive {

  public EmptyLiteral(Position position) {
    super(position);
  }

  public EmptyLiteral(Position position, String scope) {
    super(position, scope);
  }

  public EmptyLiteral() {}

  @Override
  public String toString() {
    return "<EmptyLiteral>";
  }
}
