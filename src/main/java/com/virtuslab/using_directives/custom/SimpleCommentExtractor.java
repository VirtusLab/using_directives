package com.virtuslab.using_directives.custom;

import static com.virtuslab.using_directives.custom.utils.Chars.*;

public class SimpleCommentExtractor {
  public static char USING_DIRECTIVE_INDICATOR = '>';

  char[] source;
  boolean useIndicator;

  public SimpleCommentExtractor(char[] source, boolean useIndicator) {
    this.source = source;
    this.useIndicator = useIndicator;
  }

  // for iteration
  char[] res;
  char c;
  int i;
  boolean insideSingleLineComment = false;
  boolean insideMultiLineComment = false;
  boolean insideDirective = false;

  public boolean isEndOfLine() {
    return c == FF || c == CR || c == LF;
  }

  public boolean aWhiteSpace() {
    return c == ' ' || c == '\t' || c == SU || isEndOfLine();
  }

  char next() {
    if (i + 1 < source.length) return source[i + 1];
    else return SU;
  }

  void use() {
    if (i < res.length) res[i] = source[i];
  }

  void skip() {
    if (i < res.length) res[i] = ' ';
  }

  void skipNext() {
    if (i + 1 < res.length) {
      res[i + 1] = ' ';
      i++;
    }
  }

  public char[] extractComments() {
    res = new char[source.length];
    for (i = 0; i < res.length; i++) {
      c = source[i];
      if (insideMultiLineComment) {
        if (c == '*' && next() == '/') {
          skip();
          skipNext();
          insideMultiLineComment = false;
          insideDirective = false;
        } else if (insideDirective) use();
        else skip();
      } else if (insideSingleLineComment) {
        if (isEndOfLine()) {
          insideDirective = false;
          insideSingleLineComment = false;
          use(); // whitespace, we want it
        } else if (insideDirective) use();
        else skip();
      } else if (aWhiteSpace()) use();
      else {
        if (c == '/') {
          if (next() == '/') insideSingleLineComment = true;
          if (next() == '*') insideMultiLineComment = true;

          skip();
          if (insideSingleLineComment || insideMultiLineComment) {
            skipNext();
            boolean hasIndicator = next() == USING_DIRECTIVE_INDICATOR;
            insideDirective = hasIndicator == useIndicator;
            if (useIndicator && hasIndicator) skipNext();
          }
        }
        skip();
      }
    }

    return res;
  }
}
