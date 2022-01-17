package com.virtuslab.using_directives.custom.utils;

public class Position {
  private final int line;
  private final int column;
  private final int offset;

  public Position(int line, int column, int offset) {
    this.line = line;
    this.column = column;
    this.offset = offset;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  public int getOffset() {
    return offset;
  }

  public String toString() {
    return String.format("Position(%s:%s)", line, column);
  }
}
