package com.virtuslab.using_directives.custom.utils;

import java.util.ArrayList;
import java.util.List;

public class Source {
  protected final char[] content;

  protected final List<Integer> lineStarts;

  public Source(char[] content) {
    lineStarts = calculateLineStarts(content);
    this.content = content;
  }

  public char[] getContent() {
    return content;
  }

  public Position getPositionFromOffset(int offset) {
    int lineNumber = -1;
    int columnNumber = -1;
    for (int i = lineStarts.size() - 1; i >= 0; i--) {
      if (lineStarts.get(i) <= offset) {
        lineNumber = i;
        columnNumber = offset - lineStarts.get(i);
        break;
      }
    }
    return new Position(lineNumber, columnNumber, offset);
  }

  public int translateOffset(int offset) {
    return offset;
  }

  private List<Integer> calculateLineStarts(char[] content) {
    List<Integer> result = new ArrayList<>();
    result.add(0);
    for (int i = 0; i < content.length; i++) {
      if (content[i] == Chars.LF) {
        result.add(i + 1);
      }
    }
    return result;
  }
}
