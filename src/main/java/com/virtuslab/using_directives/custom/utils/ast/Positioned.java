package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;

public abstract class Positioned {
  private Position position;

  public Positioned(Position position) {
    this.position = position;
  }

  public Positioned() {}

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }
}
