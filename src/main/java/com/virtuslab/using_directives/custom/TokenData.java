package com.virtuslab.using_directives.custom;

import static com.virtuslab.using_directives.custom.utils.TokenUtils.*;

public class TokenData {
  public Tokens token = Tokens.EMPTY;
  public int offset = 0;
  public int lastOffset = 0;
  public int lineOffset = -1;
  public String name = null;
  public String strVal = null;
  public int base = 0;

  public void copyFrom(TokenData td) {
    this.token = td.token;
    this.offset = td.offset;
    this.lastOffset = td.lastOffset;
    this.lineOffset = td.lineOffset;
    this.name = td.name;
    this.strVal = td.strVal;
    this.base = td.base;
  }

  public boolean isNewLine() {
    return token == Tokens.NEWLINE || token == Tokens.NEWLINES;
  }

  public boolean isAfterLineEnd() {
    return lineOffset >= 0;
  }

  // NotImplemented
  public boolean isOperator() {
    return false;
  }

  public String toTokenInfoString() {
    if (identifierTokens.contains(token)) {
      return String.format("%s: %s", token.str, name);
    } else if (literalTokens.contains(token)) {
      return String.format("%s: %s", token.str, strVal);
    } else {
      return String.format("%s", token.str);
    }
  }
}
