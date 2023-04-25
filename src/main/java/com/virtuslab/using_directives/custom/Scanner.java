package com.virtuslab.using_directives.custom;

import static com.virtuslab.using_directives.custom.regions.Region.topLevelRegion;
import static com.virtuslab.using_directives.custom.utils.Chars.*;
import static com.virtuslab.using_directives.custom.utils.TokenUtils.*;

import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.custom.regions.*;
import com.virtuslab.using_directives.custom.utils.Source;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Scanner {

  private final Context context;
  private Source source;
  private boolean debug = false;
  public boolean allowLeadingInfixOperators = true;

  public Scanner(Source source, int startFrom, Context context) {
    this.source = source;
    this.context = context;
    reader = new CustomCharArrayReader(source.getContent(), this::errorButContinue);
    reader.startFrom = startFrom;
    reader.nextChar();
    nextToken();
    currentRegion = topLevelRegion(indentWidth(td.offset));
  }

  public Scanner(Source source, int startFrom, Context context, boolean debug) {
    this(source, startFrom, context);
    this.debug = debug;
  }

  public Context getContext() {
    return context;
  }

  class LookaheadScanner extends Scanner {
    public LookaheadScanner() {
      super(Scanner.this.source, Scanner.this.reader.startFrom, context);
    }
  }

  public TokenData td = newTokenData();

  private final CustomCharArrayReader reader;

  private void error(String msg, int offset) {
    context.getReporter().error(source.getPositionFromOffset(offset), msg);
  }

  private void error(String msg) {
    error(msg, td.offset);
  }

  private void errorButContinue(String msg, int offset) {
    context.getReporter().error(source.getPositionFromOffset(offset), msg);
  }

  private void incompleteInputError(String msg) {
    context.getReporter().error(String.format("Incomplete input: %s", msg));
  }

  private final Deque<Character> litBuf = new LinkedList<>();

  private String getLitBufString() {
    return litBuf.stream().map(Object::toString).collect(Collectors.joining());
  }

  private void putChar(char c) {
    litBuf.addLast(c);
  }

  private void finishNamed(Tokens idtoken, TokenData target) {
    target.name = getLitBufString();
    litBuf.clear();
    target.token = idtoken;
    if (idtoken == Tokens.IDENTIFIER && keywordMap.containsKey(target.name)) {
      Tokens converted = keywordMap.get(target.name);
      if (converted != Tokens.END || target == td) {
        target.token = converted;
      }
    }
  }

  private void setStrVal() {
    td.strVal = getLitBufString();
    litBuf.clear();
  }

  private TokenData newTokenData() {
    return new TokenData();
  }

  public TokenData next = newTokenData();
  public TokenData prev = newTokenData();

  Region currentRegion = new Indented(IndentWidth.Zero(), new HashSet<>(), Tokens.EMPTY, null);

  private void dropBraces() {
    if (currentRegion instanceof InBraces) {
      currentRegion = ((InBraces) currentRegion).enclosing();
    } else {
      if (!currentRegion.isOutermost()) {
        currentRegion = currentRegion.enclosing();
        dropBraces();
      }
    }
  }

  private void adjustSepRegions(Tokens lastToken) {
    switch (lastToken) {
      case LPAREN:
      case LBRACKET:
        currentRegion = new InParens(lastToken, currentRegion);
        break;
      case LBRACE:
        currentRegion = new InBraces(currentRegion);
        break;
      case RBRACE:
        dropBraces();
        break;
      case RPAREN:
      case RBRACKET:
        if (currentRegion instanceof InParens) {
          InParens ip = (InParens) currentRegion;
          if (ip.prefix.ordinal() + 1 == lastToken.ordinal()) {
            currentRegion = ip.outer();
          }
        }
        break;
      case STRINGLIT:
        if (currentRegion instanceof InString) {
          InString is = (InString) currentRegion;
          currentRegion = is.outer();
        }
        break;
    }
  }

  public void nextToken() {
    Tokens lastToken = td.token;
    String lastName = td.name;
    adjustSepRegions(lastToken);
    if (next.token == Tokens.EMPTY) {
      td.lastOffset = reader.lastCharOffset;
      fetchToken();
      if (td.token == Tokens.ERROR) adjustSepRegions(Tokens.STRINGLIT);
    } else {
      this.td.copyFrom(next);
      next.token = Tokens.EMPTY;
    }

    if (td.isAfterLineEnd()) handleNewLine(lastToken, lastName);
    postProcessToken();
    printState();
  }

  public void printState() {
    if (debug) {
      System.out.println(show());
    }
  }

  private void insert(Tokens token, int offset) {
    assert (next.token == Tokens.EMPTY);
    next.copyFrom(td);
    td.offset = offset;
    td.token = token;
  }

  public boolean isContinuing(Tokens lastToken) {
    return (openParensTokens.contains(td.token)) && !pastBlankLine();
  }

  private IndentWidth indentWidthRecur(
      int idx, char ch, int n, Function<IndentWidth, IndentWidth> k) {
    while (idx >= 0) {
      char nextChar = reader.buf[idx];
      if (nextChar == LF) return k.apply(new Run(ch, n));
      else if (nextChar == ' ' || nextChar == '\t') {
        if (nextChar == ch) {
          idx = idx - 1;
          n = n + 1;
        } else {
          char c = ch;
          int nn = n;
          if (n != 0) k = i -> new Conc(i, new Run(c, nn));
          idx = idx - 1;
          ch = nextChar;
          n = 1;
        }
      } else {
        idx = idx - 1;
        ch = ' ';
        n = 0;
        k = i -> i;
      }
    }
    return k.apply(new Run(ch, n));
  }

  public IndentWidth indentWidth(int offset) {
    return indentWidthRecur(offset - 1, ' ', 0, i -> i);
  }

  public void handleNewLine(Tokens lastToken, String lastName) {
    boolean indentIsSignificant = false;
    boolean newlineIsSeparating = false;
    IndentWidth lastWidth = IndentWidth.Zero();
    IndentWidth nextWidth = indentWidth(td.offset);
    if (currentRegion instanceof Indented) {
      Indented i = (Indented) currentRegion;
      indentIsSignificant = true;
      lastWidth = i.width;
      newlineIsSeparating = lastWidth.lessOrEqual(nextWidth) || i.isOutermost();
    } else {
      indentIsSignificant = true;
      currentRegion.proposeKnownWidth(nextWidth, lastToken);
      lastWidth = currentRegion.knownWidth;
      newlineIsSeparating = currentRegion instanceof InBraces;
    }
    if (newlineIsSeparating
        && canEndStatTokens.contains(lastToken)
        && canStartStatTokens().contains(td.token)
        && !(lastWidth.less(nextWidth) && isContinuing(lastToken))
        && (lastName == null || !isValidUsingDirectiveStart(lastToken))) {
      if (pastBlankLine()) {
        insert(Tokens.NEWLINES, td.lineOffset);
      } else {
        insert(Tokens.NEWLINE, td.lineOffset);
      }
    } else if (indentIsSignificant) {
      if (nextWidth.less(lastWidth)) {
        if (currentRegion.isOutermost()) {
          if (nextWidth.less(lastWidth)) {
            currentRegion = topLevelRegion(nextWidth);
          }
        } else {
          if (currentRegion instanceof Indented) {
            currentRegion = currentRegion.enclosing();
            insert(Tokens.OUTDENT, td.offset);
          } else if (currentRegion instanceof InBraces && !closingRegionTokens.contains(td.token)) {
            error(String.format("Expected closing region token but found %s", td.token.str));
          }
        }
      } else if (lastWidth.less(nextWidth)) {
        if (canStartIndentTokens.contains(lastToken)) { // TODO sad_pepe hack
          currentRegion = new Indented(nextWidth, new HashSet<>(), lastToken, currentRegion);
          insert(Tokens.INDENT, td.offset);
        } else if (lastToken == Tokens.SELFARROW) {
          currentRegion.knownWidth = nextWidth;
        }
      } else if (!lastWidth.equals(nextWidth)) {
        errorButContinue(spaceTabMismatchMsg(lastWidth, nextWidth), td.offset);
      }
    }
    if (currentRegion instanceof Indented) {
      Indented i = (Indented) currentRegion;
      if (i.width.less(nextWidth) && !i.others.contains(nextWidth) && nextWidth != lastWidth) {
        if (td.token == Tokens.OUTDENT && next.token != Tokens.COLON) {
          errorButContinue(
              "The start of this line does not match any of the previous "
                  + "indentation widths. Indentation width of current line falls between previous widths.",
              td.offset);
        } else {
          Set<IndentWidth> newSet = new HashSet<>(i.others);
          newSet.add(nextWidth);
          currentRegion = new Indented(i.width, newSet, i.prefix, i.outer);
        }
      }
    }
  }

  public String spaceTabMismatchMsg(IndentWidth lastWidth, IndentWidth nextWidth) {
    return String.format(
        "Incompatible combinations of tabs and spaces in indentation prefixes.\n"
            + "Previous indent : %s\n"
            + "Latest indent   : %s",
        lastWidth, nextWidth);
  }

  public void observeColonEOL() {
    if (td.token == Tokens.COLON) {
      lookAhead();
      boolean atEOL = td.isAfterLineEnd() || td.token == Tokens.EOF;
      reset();
      if (atEOL) {
        td.token = Tokens.COLONEOL;
      }
    }
  }

  public void lookAhead() {
    prev.copyFrom(td);
    td.lastOffset = reader.lastCharOffset;
    fetchToken();
  }

  public void reset() {
    next.copyFrom(td);
    td.copyFrom(prev);
  }

  public void closeIndented() {
    if (currentRegion instanceof Indented && !currentRegion.isOutermost()) {
      insert(Tokens.OUTDENT, td.offset);
      currentRegion = currentRegion.outer();
    }
  }

  private boolean isEnclosedInParens(Region r) {
    if (r instanceof Indented) return isEnclosedInParens(r.outer());
    else return r instanceof InParens;
  }

  public void postProcessToken() {
    switch (td.token) {
      case SEMI:
        lookAhead();
        reset();
        break;
      case COMMA:
        if (currentRegion instanceof Indented && isEnclosedInParens(currentRegion.outer())) {
          insert(Tokens.OUTDENT, td.offset);
          currentRegion = currentRegion.outer();
        } else {
          lookAhead();
          if (td.isAfterLineEnd()
              && (td.token == Tokens.RPAREN
                  || td.token == Tokens.RBRACKET
                  || td.token == Tokens.RBRACE
                  || td.token == Tokens.OUTDENT)) {
          } else if (td.token == Tokens.EOF) {
          } else reset();
        }
        break;
      case END:
        td.token = Tokens.IDENTIFIER;
        break;
      case RBRACE:
      case RPAREN:
      case RBRACKET:
      case EOF:
        closeIndented();
        break;
      default:
        break;
    }
  }

  private boolean pastBlankLineRecur(int idx, boolean isBlank, int end) {
    Supplier<Boolean> expr1 =
        () -> {
          char ch = reader.buf[idx];
          if (ch == LF || ch == FF) return isBlank || pastBlankLineRecur(idx + 1, true, end);
          else return pastBlankLineRecur(idx + 1, isBlank && ch <= ' ', end);
        };
    return idx < end && expr1.get();
  }

  private boolean pastBlankLine() {
    int end = td.offset;
    return pastBlankLineRecur(td.lastOffset, false, end);
  }

  private void fetchToken() {
    while (doFetchToken()) {}
    ;
  }

  private boolean doFetchToken() {
    Character ch = null;
    do {
      if (ch != null) reader.nextChar();
      td.offset = reader.charOffset - 1;
      td.lineOffset = td.lastOffset < reader.lineStartOffset ? reader.lineStartOffset : -1;
      td.name = null;
      ch = reader.ch;
    } while (ch == ' ' || ch == '\t' || ch == CR || ch == LF || ch == FF);
    if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '$' || ch == '_') {
      putChar(ch);
      reader.nextChar();
      getIdentRest();
      if (reader.ch == '\"' && td.token == Tokens.IDENTIFIER) td.token = Tokens.INTERPOLATIONID;
    } else if (ch == '<') {
      // Not supported: XMLSTART
      putChar('<');
      reader.nextChar();
      getOperatorRest();
    } else if (ch == '~'
        || ch == '!'
        || ch == '@'
        || ch == '#'
        || ch == '%'
        || ch == '^'
        || ch == '*'
        || ch == '+'
        || ch == '-'
        || ch == '>'
        || ch == '?'
        || ch == ':'
        || ch == '='
        || ch == '&'
        || ch == '|'
        || ch == '\\') {
      putChar(ch);
      reader.nextChar();
      getOperatorRest();
    } else if (ch == '/') {
      if (skipComment()) return true;
      else {
        putChar('/');
        getOperatorRest();
      }
    } else if (ch == '`') {
      getBackquotedIdent();
    } else if (ch == '\"') {
      Runnable fetchDoubleQuote =
          () -> {
            reader.nextChar();
            if (reader.ch == '\"') {
              reader.nextChar();
              if (reader.ch == '\"') {
                reader.nextRawChar();
                getRawStringLit();
              } else {
                td.token = Tokens.STRINGLIT;
                td.strVal = "";
              }
            } else {
              getStringLit();
            }
          };
      fetchDoubleQuote.run();
    } else if (ch == '\'') {
      reader.nextChar();
      if (isIdentifierStart(reader.ch)) {
        charLitOr(
            () -> {
              getIdentRest();
              return Tokens.QUOTEID;
            });
      } else if (isIdentifierPart(reader.ch) && reader.ch != '\\') {
        charLitOr(
            () -> {
              getOperatorRest();
              return Tokens.QUOTEID;
            });
      } else {
        switch (reader.ch) {
          case '{':
          case '[':
          case ' ':
          case '\t':
            if (reader.lookaheadChar() != '\'') td.token = Tokens.QUOTE;
            break;
          default:
            if (reader.isAtEnd()
                && (reader.ch != SU && reader.ch != CR && reader.ch != LF
                    || reader.isUnicodeEscape())) {
              boolean isEmptyCharLit = reader.ch == '\'';
              getLitChar();
              if (reader.ch == '\'') {
                if (isEmptyCharLit) {
                  error("empty character literal (use '\\'' for single quote)");
                } else {
                  finishCharLit();
                }
              } else if (isEmptyCharLit) {
                error("empty character literal");
              } else {
                error("unclosed character literal");
              }
            } else {
              error("unclosed character literal");
            }
        }
      }
    } else if (ch == ',') {
      reader.nextChar();
      td.token = Tokens.COMMA;
    } else if (ch == SU) {
      if (reader.isAtEnd()) td.token = Tokens.EOF;
      else {
        error("illegal character");
        reader.nextChar();
        td.token = Tokens.ERROR;
      }
    } else {
      putChar(ch);
      reader.nextChar();
      getOperatorRest();
    }
    return false;
  }

  // Unsupported: Keeping comments
  private boolean skipComment() {
    Runnable skipLine =
        () -> {
          reader.nextChar();
          while (reader.ch != CR && reader.ch != LF && reader.ch != SU) {
            reader.nextChar();
          }
        };
    Runnable skipComment =
        () -> {
          int nested = 0;
          boolean flag = true;
          while (flag) {
            if (reader.ch == '/') {
              reader.nextChar();
              if (reader.ch == '*') {
                nested += 1;
                reader.nextChar();
              }
            } else if (reader.ch == '*') {
              reader.nextChar();
              if (reader.ch == '/') {
                if (nested > 0) {
                  nested -= 1;
                } else flag = false;
                reader.nextChar();
              }
            } else {
              reader.nextChar();
            }
          }
        };
    reader.nextChar();
    if (reader.ch == '/') {
      skipLine.run();
      return true;
    } else if (reader.ch == '*') {
      reader.nextChar();
      skipComment.run();
      return true;
    } else {
      return false;
    }
  }

  public TokenData lookahead() {
    if (next.token == Tokens.EMPTY) {
      lookAhead();
      reset();
    }
    return next;
  }

  public void getBackquotedIdent() {
    reader.nextChar();
    getLitChars('`');
    if (reader.ch == '`') {
      reader.nextChar();
      finishNamed(Tokens.IDENTIFIER, td);
      if (td.name.length() == 0) {
        error("empty quoted identifier");
      } else if (td.name.contains("_")) {
        error("wildcard invalid as backquoted identifier");
      }
    } else {
      error("unclosed quoted identifier");
    }
  }

  public void getIdentRest() {
    if (!Character.isWhitespace(reader.ch) && reader.ch != ',' && !reader.isAtEnd()) {
      putChar(reader.ch);
      reader.nextChar();
      getIdentRest();
    } else {
      finishNamed(Tokens.IDENTIFIER, td);
    }
  }

  public void getOperatorRest() {
    switch (reader.ch) {
      case '~':
      case '!':
      case '@':
      case '#':
      case '%':
      case '^':
      case '*':
      case '+':
      case '-':
      case '<':
      case '>':
      case '?':
      case ':':
      case '=':
      case '&':
      case '|':
      case '\\':
        putChar(reader.ch);
        reader.nextChar();
        getOperatorRest();
        break;
      case '/':
        char nxch = reader.lookaheadChar();
        if (nxch == '/' || nxch == '*') finishNamed(Tokens.IDENTIFIER, td);
        else {
          putChar(reader.ch);
          reader.nextChar();
          getOperatorRest();
        }
        break;
      default:
        if (isSpecial(reader.ch)) {
          putChar(reader.ch);
          reader.nextChar();
          getOperatorRest();
        } else getIdentRest();
        break;
    }
  }

  public void getIdentOrOperatorRest() {
    if (isIdentifierPart(reader.ch)) getIdentRest();
    else {
      switch (reader.ch) {
        case '~':
        case '!':
        case '@':
        case '#':
        case '%':
        case '^':
        case '*':
        case '+':
        case '-':
        case '<':
        case '>':
        case '?':
        case ':':
        case '=':
        case '&':
        case '|':
        case '\\':
        case '/':
          getOperatorRest();
          break;
        default:
          if (isSpecial(reader.ch)) {
            getOperatorRest();
          } else getIdentRest();
          break;
      }
    }
  }

  public Set<Tokens> canStartStatTokens() {
    return canStartStatTokens3;
  }

  public Set<Tokens> canStartExprTokens() {
    return canStartExprTokens3;
  }

  public void getStringLit() {
    getLitChars('"');
    if (reader.ch == '"') {
      setStrVal();
      reader.nextChar();
      td.token = Tokens.STRINGLIT;
    } else {
      // try to recover from started string
      setStrVal();
      td.token = Tokens.STRINGLIT;
      error("unclosed string literal");
    }
  }

  public void getRawStringLit() {
    if (reader.ch == '"') {
      reader.nextRawChar();
      if (isTripleQuote()) {
        setStrVal();
        td.token = Tokens.STRINGLIT;
      } else {
        getRawStringLit();
      }
    } else if (reader.ch == SU) {
      incompleteInputError("unclosed multi-line string literal");
    } else {
      putChar(reader.ch);
      reader.nextRawChar();
      getRawStringLit();
    }
  }

  public boolean isTripleQuote() {
    if (reader.ch == '"') {
      reader.nextRawChar();
      if (reader.ch == '"') {
        reader.nextChar();
        while (reader.ch == '"') {
          putChar('"');
          reader.nextChar();
        }
        return true;
      } else {
        putChar('"');
        putChar('"');
        return false;
      }
    } else {
      putChar('"');
      return false;
    }
  }

  public void getLitChar() {
    Runnable invalidUnicodeEscape =
        () -> {
          error("invalid character in unicode escape sequence", reader.charOffset - 1);
          putChar(reader.ch);
        };
    Runnable putUnicode =
        () -> {
          while (reader.ch == 'u' || reader.ch == 'U') {
            reader.nextChar();
          }
          int i = 0;
          int cp = 0;
          while (i < 4) {
            int shift = (3 - i) * 4;
            int d = digitToInt(reader.ch, 16);
            if (d < 0) {
              invalidUnicodeEscape.run();
              return;
            }
            cp += (d << shift);
            reader.nextChar();
            i += 1;
          }
          putChar((char) cp);
        };

    if (reader.ch == '\\') {
      reader.nextChar();
      if ('0' <= reader.ch && reader.ch <= '7') {
        int start = reader.charOffset - 2;
        char leadch = reader.ch;
        int oct = digitToInt(reader.ch, 8);
        reader.nextChar();
        if ('0' <= reader.ch && reader.ch <= '7') {
          oct = oct * 8 + digitToInt(reader.ch, 8);
          reader.nextChar();
          if (leadch <= '3' && '0' <= reader.ch && reader.ch <= '7') {
            oct = oct * 8 + digitToInt(reader.ch, 8);
            reader.nextChar();
          }
        }
        String alt = oct == LF ? "\\n" : String.format("\\u%04x", oct);
        error(String.format("octal escape literals are unsupported: use %s instead", alt));
        putChar((char) oct);
      } else if (reader.ch == 'u' || reader.ch == 'U') {
        putUnicode.run();
      } else {
        switch (reader.ch) {
          case 'b':
            putChar('\b');
            break;
          case 't':
            putChar('\t');
            break;
          case 'n':
            putChar('\n');
            break;
          case 'f':
            putChar('\f');
            break;
          case 'r':
            putChar('\r');
            break;
          case '\"':
            putChar('\"');
            break;
          case '\'':
            putChar('\'');
            break;
          case '\\':
            putChar('\\');
            break;
          default:
            invalidEscape();
        }
        reader.nextChar();
      }
    } else {
      putChar(reader.ch);
      reader.nextChar();
    }
  }

  public void invalidEscape() {
    error("invalid escape character", reader.charOffset - 1);
    putChar(reader.ch);
  }

  public void getLitChars(char delimiter) {
    while (reader.ch != delimiter
        && !reader.isAtEnd()
        && (reader.ch != SU && reader.ch != CR && reader.ch != LF || reader.isUnicodeEscape())) {
      getLitChar();
    }
  }

  public void finishCharLit() {
    reader.nextChar();
    td.token = Tokens.CHARLIT;
    setStrVal();
  }

  public void charLitOr(Supplier<Tokens> op) {
    putChar(reader.ch);
    reader.nextChar();
    if (reader.ch == '\'') finishCharLit();
    else {
      td.token = op.get();
      td.strVal = td.name;
      litBuf.clear();
    }
  }

  @Override
  public String toString() {
    String s1 = td.token.showTokenDetailed();
    String s2;
    if (identifierTokens.contains(td.token)) s2 = String.format(" %s", td.name);
    else if (literalTokens.contains(td.token)) s2 = String.format(" %s", td.strVal);
    else s2 = "";
    return s1 + s2;
  }

  public String show() {
    switch (td.token) {
      case IDENTIFIER:
      case BACKQUOTED_IDENT:
        return String.format("id(%s)", td.name);
      case CHARLIT:
        return String.format("char(%s)", td.strVal);
      case INTLIT:
        return String.format("int(%s, base=%d)", td.strVal, td.base);
      case LONGLIT:
        return String.format("long(%s, base=%d)", td.strVal, td.base);
      case FLOATLIT:
        return String.format("float(%s)", td.strVal);
      case DOUBLELIT:
        return String.format("double(%s)", td.strVal);
      case STRINGLIT:
        return String.format("string(%s)", td.strVal);
      case STRINGPART:
        return String.format("stringpart(%s)", td.strVal);
      case INTERPOLATIONID:
        return String.format("interpolationid(%s)", td.name);
      case SEMI:
      case NEWLINE:
        return ";";
      case NEWLINES:
        return ";;";
      case COMMA:
        return ",";
      default:
        return td.token.showToken();
    }
  }
}
