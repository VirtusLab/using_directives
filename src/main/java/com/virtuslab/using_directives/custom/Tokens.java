package com.virtuslab.using_directives.custom;

import static com.virtuslab.using_directives.custom.utils.TokenUtils.keywords;

public enum Tokens {
  EMPTY("<empty>"),
  ERROR("erroneous token"),
  EOF("eof"),
  CHARLIT("character literal"),
  INTLIT("integer literal"),
  DECILIT("number literal"),
  EXPOLIT("number literal with exponent"),
  LONGLIT("long literal"),
  FLOATLIT("float literal"),
  DOUBLELIT("double literal"),
  STRINGLIT("string literal"),
  STRINGPART("string literal", "string literal part"),
  IDENTIFIER("identifier"),
  USING("using"),
  REQUIRE("require"),
  NULL("null"),
  TRUE("true"),
  FALSE("false"),
  COMMA("','"),
  SEMI("';'"),
  DOT("'.'"),
  COLON(":"),
  EQUALS("="),
  AT("@"),
  LPAREN("'('"),
  RPAREN("')'"),
  LBRACKET("'['"),
  RBRACKET("']'"),
  LBRACE("'{'"),
  RBRACE("'}'"),
  INDENT("indentation"),
  OUTDENT("outdentation"),
  INTERPOLATIONID("string interpolator"),
  QUOTEID("quoted identifier"),
  BACKQUOTED_IDENT("identifier", "backquoted ident"),
  END("end"),
  NEWLINE("end of statement", "new line"),
  NEWLINES("end of statement", "new lines"),
  USCORE("_"),
  ATUSING("@using"),
  COLON_USING("@using"),
  ATREQUIRE("@require"),
  CTXARROW("?=>"),
  QUOTE("'"),
  COLONEOL(":", ": at eol"),
  SELFARROW("=>");

  Tokens(String str, String debugStr) {
    this.str = str;
    this.debugStr = str;
  }

  Tokens(String str) {
    this.str = str;
    this.debugStr = str;
  }

  public final String str;
  public final String debugStr;

  public String showTokenDetailed() {
    return debugStr;
  }

  public String showToken() {
    String str = this.str;
    if (isKeyword()) return String.format("'%s'", str);
    else return str;
  }

  private boolean isKeyword() {
    return keywords.contains(this);
  }
}
