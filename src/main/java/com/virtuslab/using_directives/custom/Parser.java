package com.virtuslab.using_directives.custom;

import static com.virtuslab.using_directives.custom.utils.TokenUtils.*;

import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.*;
import com.virtuslab.using_directives.reporter.Reporter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Parser {

  private Source source;

  private final Reporter reporter;

  public Parser(Source source, Reporter reporter) {
    this.reporter = reporter;
    this.source = source;
    this.in = new Scanner(source, 0, reporter);
  }

  public Reporter getReporter() {
    return reporter;
  }

  Scanner in;

  /* Combinators */

  private void error(String msg, int offset) {
    reporter.error(source.getPositionFromOffset(offset), msg);
  }

  private void error(String msg) {
    error(msg, offset(in.td.offset));
  }

  private int offset(int off) {
    return source.translateOffset(off);
  }

  public void accept(Tokens token) {
    if (in.td.token == token) {
      in.nextToken();
    } else {
      error(String.format("Expected token %s but found %s", token.str, in.td.token.str));
    }
  }

  public <T> T enclosed(Tokens token, Supplier<T> callback) {
    accept(token);
    try {
      return callback.get();
    } finally {
      accept(tokenFromInt(token.ordinal() + 1));
    }
  }

  public <T> T inBracesOrIndented(Supplier<T> callback) {
    switch (in.td.token) {
      case INDENT:
        return enclosed(Tokens.INDENT, callback);
      case LBRACE:
        return enclosed(Tokens.LBRACE, callback);
      default:
        error(String.format("Expected indent or braces but found %s", in.td.token.str));
        return callback.get();
    }
  }

  public void newLineOptWhenFollowedBy(Tokens token) {
    if (in.td.token == Tokens.NEWLINE && in.next.token == token) {
      in.nextToken();
    }
  }

  public UsingDefs parse() {
    UsingDefs t = usingDirectives();

    return t;
  }

  UsingDefs usingDirectives() {
    ArrayList<UsingDef> usingTrees = new ArrayList<>();
    int codeOffset = 0;
    UsingDef ud = usingDirective();
    int offset = offset(in.td.offset);
    while (ud != null) {
      usingTrees.add(ud);
      codeOffset = offset(in.td.lastOffset);
      TokenData tokenData = in.td;
      if (tokenData.isNewLine()) {
        in.nextToken();
      }
      if (tokenData.token != Tokens.EOF && !tokenData.isAfterLineEnd()) {
        error(
            String.format(
                "Expected new line after the using directive, in the line; but found %s",
                tokenData.toTokenInfoString()));
        ud = null;
      } else {
        ud = usingDirective();
      }
    }
    return new UsingDefs(
        usingTrees, codeOffset, offset(in.td.offset), source.getPositionFromOffset(offset));
  }

  UsingDef usingDirective() {
    if (isValidUsingDirectiveStart(in.td.token)) {
      int offset = offset(in.td.offset);
      in.nextToken();
      return new UsingDef(settings(), source.getPositionFromOffset(offset));
    }
    return null;
  }

  private List<SettingDef> parseSettings() {
    if (in.td.token == Tokens.IDENTIFIER) {
      List<SettingDef> settings = new ArrayList<>();
      settings.add(setting());
      settings.addAll(parseSettings());
      return settings;
    } else {
      return new ArrayList<>();
    }
  }

  // > using
  // > oneKey ...
  // > secondKey ...
  // > thirdKey {
  // > nestedKey1
  // > nestedKey2
  // > }
  SettingDefs settings() {
    ArrayList<SettingDef> settings = new ArrayList<>();
    int offset = offset(in.td.offset);
    if (in.td.token == Tokens.IDENTIFIER) {
      settings.add(setting());
    } else {
      settings.addAll(inBracesOrIndented(this::parseSettings));
    }
    return new SettingDefs(settings, source.getPositionFromOffset(offset));
  }

  SettingDef setting() {
    int offset = offset(in.td.offset);
    String key = key();
    SettingDefOrUsingValue value = valueOrSetting(offset + key.length());
    return new SettingDef(key, value, source.getPositionFromOffset(offset));
  }

  String key() {
    if (in.td.token == Tokens.IDENTIFIER) {
      String key = in.td.name;
      in.nextToken();
      if (in.td.token == Tokens.DOT) {
        in.nextToken();
        return key + "." + key();
      } else {
        return key;
      }
    }
    error(String.format("Expected identifier but found %s", in.td.token.str));
    return null;
  }

  SettingDefOrUsingValue valueOrSetting(int keyEnd) {
    return value(keyEnd);
  }

  UsingValue value(int keyEnd) {
    int offset = offset(in.td.offset);
    boolean isAfterLineEnd = in.td.isAfterLineEnd();
    UsingPrimitive p = primitive(keyEnd);

    if (isAfterLineEnd) {
      return new EmptyLiteral(source.getPositionFromOffset(keyEnd));
    } else if (in.td.token != Tokens.EOF && !(in.td.isAfterLineEnd())) {
      int commaIndex = in.td.offset;

      if (in.td.token == Tokens.COMMA) {
        in.nextToken();
      }
      UsingValue rest = value(commaIndex);
      if (rest instanceof UsingPrimitive) {
        ArrayList<UsingPrimitive> res = new ArrayList<>();
        res.add(p);
        if (!(rest instanceof EmptyLiteral)) {
          res.add((UsingPrimitive) rest);
        }
        return new UsingValues(res, source.getPositionFromOffset(offset));
      } else {
        ((UsingValues) rest).getValues().add(0, p);
        return rest;
      }
    } else {
      return p;
    }
  }

  UsingPrimitive primitive(int keyEnd) {
    int offset = offset(in.td.offset);
    UsingPrimitive res = null;
    if (in.td.token == Tokens.STRINGLIT) {
      res = new StringLiteral(in.td.strVal, source.getPositionFromOffset(offset));
      in.nextToken();
    } else if (in.td.token == Tokens.IDENTIFIER) {
      res = new StringLiteral(in.td.name, source.getPositionFromOffset(offset));
      in.nextToken();
    } else if (in.td.token == Tokens.TRUE) {
      res = new BooleanLiteral(true, source.getPositionFromOffset(offset));
      in.nextToken();
    } else if (in.td.token == Tokens.FALSE) {
      res = new BooleanLiteral(false, source.getPositionFromOffset(offset));
      in.nextToken();
    } else {
      res = new EmptyLiteral(source.getPositionFromOffset(keyEnd));
    }
    return res;
  }
}
