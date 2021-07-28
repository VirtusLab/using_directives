package dotty.using_directives.custom;

import dotty.using_directives.custom.regions.*;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dotty.using_directives.custom.Tokens.*;
import static dotty.using_directives.custom.regions.Region.topLevelRegion;
import static dotty.using_directives.custom.utils.Chars.*;
import static dotty.using_directives.custom.utils.TokenUtils.*;

import java.util.concurrent.TimeUnit;

public class Scanner {

    private boolean debug = false;
    public boolean allowLeadingInfixOperators = true;

    public Scanner(char[] source, int startFrom) {
        reader = new CustomCharArrayReader(source, this::errorButContinue);
        reader.startFrom = startFrom;
        reader.nextChar();
        nextToken();
        currentRegion = topLevelRegion(indentWidth(td.offset));
    }

    public Scanner(char[] source, int startFrom, boolean debug) {
        this(source, startFrom);
        this.debug = debug;
    }


    class LookaheadScanner extends Scanner {
        public LookaheadScanner() {
            super(Scanner.this.reader.buf, Scanner.this.reader.startFrom);
        }
    }

    public TokenData td = newTokenData();

    private final CustomCharArrayReader reader;

    private int errOffset = -1;

    private void error(String msg, int offset) {
        System.out.println(msg);
    }

    private void error(String msg) {
        error(msg, td.offset);
    }

    private void errorButContinue(String msg, int offset) {
        System.out.println(msg);
    }

    private void incompleteInputError(String msg) {
        System.out.println(msg);
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
        if(idtoken == IDENTIFIER && keywordMap.containsKey(target.name)) {
            Tokens converted = keywordMap.get(target.name);
            if(converted != END || target == td) {
                target.token = converted;
            }
        }
    }

    private void setStrVal() {
        td.strVal = getLitBufString();
        litBuf.clear();
    }

    private boolean isNumberSeparator(char c) {
        return c == '_';
    }

    private String removeNumberSeparators(String s) {
        if (s.indexOf('_') == -1) {
            return s;
        } else {
            return s.replace("_", "");
        }
    }

    private void checkNoTrailingSeparator() {
        if (!litBuf.isEmpty() && isNumberSeparator(litBuf.getLast())) {
            errorButContinue("Trailing separator is not allowed", td.offset + litBuf.size() - 1);
        }
    }

    private TokenData newTokenData() {
        return new TokenData();
    }

    public TokenData next = newTokenData();
    public TokenData prev = newTokenData();

    Region currentRegion = new Indented(IndentWidth.Zero(), new HashSet<>(), Tokens.EMPTY, null);

    private boolean inMultiLineInterpolation() {
        if(currentRegion instanceof InString) {
            return ((InString) currentRegion).multiLine;
        } else return false;
    }

    private boolean inMultiLineInterpolatedExpression() {
        if(currentRegion instanceof InBraces) {
            InBraces ib = (InBraces) currentRegion;
            if(ib.outer() instanceof InString) {
                return ((InString) ib.outer()).multiLine;
            } else return false;
        } else return false;
    }

    private int skipToken() {
        int off = td.offset;
        nextToken();
        return off;
    }

    private <T> T skipToken(T result) {
        nextToken();
        return result;
    }

    private void dropBraces() {
        if(currentRegion instanceof InBraces) {
            currentRegion = ((InBraces) currentRegion).enclosing();
        } else {
            if(!currentRegion.isOutermost()) {
                currentRegion = currentRegion.enclosing();
                dropBraces();
            }
        }
    }

    private void adjustSepRegions(Tokens lastToken) {
        switch(lastToken) {
            case LPAREN:
            case LBRACKET:
                currentRegion = new InParens(lastToken, currentRegion);
                break;
            case LBRACE:
                dropBraces();
                break;
            case RPAREN:
            case RBRACKET:
                if(currentRegion instanceof InParens) {
                    InParens ip = (InParens) currentRegion;
                    if(ip.prefix.ordinal() + 1 == lastToken.ordinal()) {
                        currentRegion = ip.outer();
                    }
                }
                break;
            case STRINGLIT:
                if(currentRegion instanceof InString) {
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
        if(next.token == Tokens.EMPTY) {
            td.lastOffset = reader.lastCharOffset;
            if(currentRegion instanceof InString && lastToken != Tokens.STRINGPART) {
                fetchStringPart(((InString) currentRegion).multiLine);
            } else fetchToken();
            if (td.token == Tokens.ERROR) adjustSepRegions(Tokens.STRINGLIT);
        } else {
            this.td.copyFrom(next);
            next.token = Tokens.EMPTY;
        }

        if(td.isAfterLineEnd()) handleNewLine(lastToken, lastName);
        postProcessToken();
        printState();
    }

    public void printState() {
        if(debug) {
            System.out.println(show());
        }
    }

    private void insert(Tokens token, int offset) {
        assert(next.token == Tokens.EMPTY);
        next.copyFrom(td);
        td.offset = offset;
        td.token = token;
    }

    public boolean isLeadingInfixOperator(IndentWidth nextWidth, boolean isConditional) {
        Function<TokenData, Boolean> assumeStartsExpr = lexeme ->
                (canStartExprTokens().contains(lexeme.token) || lexeme.token == Tokens.COLONEOL)&& !lexeme.isOperator();

        Supplier<Boolean> expr1 = () -> {
            Scanner lookeaheadScanner = new LookaheadScanner();
            lookeaheadScanner.allowLeadingInfixOperators = false;
            lookeaheadScanner.nextToken();
            return assumeStartsExpr.apply(lookeaheadScanner.td)
                || lookeaheadScanner.td.token == Tokens.NEWLINE
                && assumeStartsExpr.apply(lookeaheadScanner.next)
                && indentWidth(td.offset).lessOrEqual(indentWidth(lookeaheadScanner.next.offset));
        };

        Supplier<Boolean> expr2 = () -> {
          if(currentRegion instanceof Indented) {
              Indented i = (Indented) currentRegion;
              boolean cond;
              if(i.outer == null) {
                  cond = true;
              } else if(i.outer() instanceof Indented) {
                  Indented i2 = (Indented) i.outer();
                  cond = i2.width.less(nextWidth) && !i2.others.contains(nextWidth);
              } else {
                  cond = i.outer().indentWidth().less(nextWidth);
              }
              return i.width.lessOrEqual(nextWidth) || cond;
          } else return true;
        };

        return allowLeadingInfixOperators
                && td.isOperator()
                && isWhitespace(reader.ch)
                && !pastBlankLine()
                && expr1.get()
                && expr2.get();

    }

    public boolean isContinuing(Tokens lastToken) {
        return (openParensTokens.contains(td.token) || lastToken == Tokens.RETURN)
                && !pastBlankLine();
    }

    private IndentWidth indentWidthRecur(int idx, char ch, int n, Function<IndentWidth, IndentWidth> k) {
        if(idx < 0)  return k.apply(new Run(ch, n));
        else {
            char nextChar = reader.buf[idx];
            if (nextChar == LF) return k.apply(new Run(ch, n));
            else if (nextChar == ' ' || nextChar == '\t') {
                if(nextChar == ch) return indentWidthRecur(idx - 1, ch, n + 1, k);
                else {
                    Function<IndentWidth, IndentWidth> k1;
                    if(n == 0) k1 = k;
                    else k1 = i -> new Conc(i, new Run(ch, n));
                    return indentWidthRecur(idx - 1, nextChar, 1, k1);
                }
            }
            else {
                return indentWidthRecur(idx - 1, ' ', 0, i -> i);
            }
        }
    }

    public IndentWidth indentWidth(int offset) {
        return indentWidthRecur(offset - 1, ' ', 0, i -> i);
    }


    public void handleNewLine(Tokens lastToken, String lastName) {
        boolean indentIsSignificant = false;
        boolean newlineIsSeparating = false;
        IndentWidth lastWidth = IndentWidth.Zero();
        Tokens indentPrefix = Tokens.EMPTY;
        IndentWidth nextWidth = indentWidth(td.offset);
        if(currentRegion instanceof Indented) {
            Indented i = (Indented) currentRegion;
            indentIsSignificant = true;
            lastWidth = i.width;
            newlineIsSeparating = lastWidth.lessOrEqual(nextWidth) || i.isOutermost();
            indentPrefix = i.prefix;
        } else {
            indentIsSignificant = true;
            currentRegion.proposeKnownWidth(nextWidth, lastToken);
            lastWidth = currentRegion.knownWidth;
            newlineIsSeparating = currentRegion instanceof InBraces;
        }

        if(newlineIsSeparating
                && canEndStatTokens.contains(lastToken)
                && canStartStatTokens().contains(td.token)
                && !isLeadingInfixOperator(nextWidth, true)
                && !(lastWidth.less(nextWidth) && isContinuing(lastToken))
                && (lastName == null || !lastName.equals("using")) //TODO sad_pepe hack
        ) {
            if(pastBlankLine()) {
                insert(Tokens.NEWLINES, td.lineOffset);
            } else {
                insert(Tokens.NEWLINE, td.lineOffset);
            }
        } else if (indentIsSignificant) {
            if(nextWidth.less(lastWidth)
                || nextWidth.equals(lastWidth) && (indentPrefix == Tokens.MATCH || indentPrefix == Tokens.CATCH) && td.token != Tokens.CASE
            ) {
                if(currentRegion.isOutermost()) {
                    if(nextWidth.less(lastWidth)) {
                        currentRegion = topLevelRegion(nextWidth);
                    }
                } else if(!isLeadingInfixOperator(nextWidth, true) && !statCtdTokens.contains(lastToken)) {
                    if(currentRegion instanceof Indented) {
                        currentRegion = currentRegion.enclosing();
                        insert(OUTDENT, td.offset);
                    } else if(currentRegion instanceof InBraces && !closingRegionTokens.contains(td.token)) {
                        // TODO: Implement reporting
                    }
                }
            } else if(lastWidth.less(nextWidth)
                || lastWidth.equals(nextWidth) && (indentPrefix == Tokens.MATCH || indentPrefix == Tokens.CATCH) && td.token != Tokens.CASE
            ) {
                if(canStartIndentTokens.contains(lastToken) || (lastName != null && lastName.equals("using"))) { //TODO sad_pepe hack
                    currentRegion = new Indented(nextWidth, new HashSet<>(), lastToken, currentRegion);
                    insert(Tokens.INDENT, td.offset);
                } else if(lastToken == Tokens.SELFARROW) {
                    currentRegion.knownWidth = nextWidth;
                }
            } else if(!lastWidth.equals(nextWidth)) {
                errorButContinue(spaceTabMismatchMsg(lastWidth, nextWidth), td.offset);
            }
        }
        if(currentRegion instanceof Indented) {
            Indented i = (Indented) currentRegion;
            if(i.width.less(nextWidth) && !i.others.contains(nextWidth) && nextWidth != lastWidth) {
                if(td.token == OUTDENT && next.token != Tokens.COLON) {
                    errorButContinue("The start of this line does not match any of the previous " +
                            "indentation widths. Indentation width of current line : $nextWidth. " +
                           "This falls between previous widths: $curWidth and $lastWidth", td.offset);
                } else {
                    Set<IndentWidth> newSet = new HashSet<>(i.others);
                    newSet.add(nextWidth);
                    currentRegion = new Indented(i.width, newSet, i.prefix, i.outer);
                }
            }
        }
    }

    public String spaceTabMismatchMsg(IndentWidth lastWidth, IndentWidth nextWidth) {
        return String.format("Incompatible combinations of tabs and spaces in indentation prefixes.\n" +
                "Previous indent : %s\n" +
                "Latest indent   : %s", lastWidth, nextWidth);
    }

    public void observeColonEOL() {
        if(td.token == Tokens.COLON) {
            lookAhead();
            boolean atEOL = td.isAfterLineEnd() || td.token == Tokens.EOF;
            reset();
            if(atEOL) {
                td.token = Tokens.COLONEOL;
            }
        }
    }

    public void observeIndented() {
        if(td.isNewLine()) {
            IndentWidth nextWidth = indentWidth(next.offset);
            IndentWidth lastWidth = currentRegion.indentWidth();
            if(lastWidth.less(nextWidth)) {
                currentRegion = new Indented(nextWidth, new HashSet<>(), Tokens.COLONEOL, currentRegion);
                td.offset = next.offset;
                td.token = INDENT;
            }
        }
    }

    public void observeOutdented() {
        if(currentRegion instanceof Indented
            && currentRegion.isOutermost()
            && closingRegionTokens.contains(td.token)
            && !(td.token == Tokens.CASE && ((Indented) currentRegion).prefix == Tokens.MATCH)
            && next.token == Tokens.EMPTY
        ) {
            currentRegion = currentRegion.enclosing();
            insert(OUTDENT, td.offset);
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
        if(currentRegion instanceof Indented && !currentRegion.isOutermost()) {
            insert(OUTDENT, td.offset);
            currentRegion = currentRegion.outer();
        }
    }

    private boolean isEnclosedInParens(Region r) {
        if(r instanceof Indented) return isEnclosedInParens(r.outer());
        else return r instanceof InParens;
    }

    public void postProcessToken() {
        Consumer<Tokens> fuse = token -> {
            td.token = token;
            td.offset = prev.offset;
            td.lastOffset = prev.lastOffset;
            td.lineOffset = prev.lineOffset;
        };
        switch(td.token){
            case CASE:
                lookAhead();
                if(td.token == CLASS) fuse.accept(CASECLASS);
                else if(td.token == OBJECT) fuse.accept(CASEOBJECT);
                else reset();
                break;
            case SEMI:
                lookAhead();
                if (td.token != ELSE) reset();
                break;
            case COMMA:
                if(currentRegion instanceof Indented && isEnclosedInParens(currentRegion.outer())) {
                    insert(OUTDENT, td.offset);
                    currentRegion = currentRegion.outer();
                } else {
                    lookAhead();
                    if(td.isAfterLineEnd()
                        && (td.token == RPAREN || td.token == RBRACKET || td.token == RBRACE || td.token == OUTDENT)
                    ) { }
                    else if (td.token == EOF) { }
                    else reset();
                }
                break;
            case END:
                if(!isEndMarker()) {
                    td.token = IDENTIFIER;
                }
                break;
            case COLON:
                observeColonEOL();
                break;
            case RBRACE:
            case RPAREN:
            case RBRACKET:
                closeIndented();
                break;
            case EOF:
                // if !source.maybeIncomplete then closeIndented()
            default:
                break;

        }
    }

    public boolean isEndMarker() {
        if(td.isAfterLineEnd()) {
            Scanner lookahead = new LookaheadScanner() {
                @Override
                public boolean isEndMarker() {
                    return false;
                }
            };
            lookahead.nextToken();
            if(endMarkerTokens.contains(lookahead.td.token)) {
                lookahead.nextToken();
                return lookahead.td.token == EOF;
            }
        }
        return false;
    }

    private boolean pastBlankLineRecur(int idx, boolean isBlank, int end) {
        Supplier<Boolean> expr1 = () -> {
            char ch = reader.buf[idx];
            if(ch == LF || ch == FF) return isBlank || pastBlankLineRecur(idx + 1, true, end);
            else return pastBlankLineRecur(idx + 1, isBlank && ch <= ' ', end);
        };
        return idx < end && expr1.get();
    }

    private boolean pastBlankLine() {
        int end = td.offset;
        return pastBlankLineRecur(td.lastOffset, false, end);
    }

    private void fetchToken() {
        td.offset = reader.charOffset - 1;
        td.lineOffset = td.lastOffset < reader.lineStartOffset ? reader.lineStartOffset : -1;
        td.name = null;
        char ch = reader.ch;
        if(ch == ' ' || ch == '\t' || ch == CR || ch == LF || ch == FF) {
            reader.nextChar();
            fetchToken();
        }
        else if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '$' || ch == '_') {
            putChar(ch);
            reader.nextChar();
            getIdentRest();
            if(reader.ch == '\"' && td.token == IDENTIFIER) td.token = INTERPOLATIONID;
        }
        else if(ch == '<') {
            // Not supported: XMLSTART
            putChar('<');
            reader.nextChar();
            getOperatorRest();
        } else if(ch == '~' || ch == '!' || ch == '@' || ch == '#' || ch == '%'
                || ch == '^' || ch == '*' || ch == '+' || ch == '-' || ch == '>'
                || ch == '?' || ch == ':' || ch == '=' || ch == '&' || ch == '|'
                || ch == '\\') {
            putChar(ch);
            reader.nextChar();
            getOperatorRest();
        } else if(ch == '/') {
            if(skipComment()) fetchToken();
            else {
                putChar('/');
                getOperatorRest();
            }
        } else if(ch == '0') {
            reader.nextChar();
            switch(reader.ch) {
                case 'x':
                case 'X':
                    td.base = 16;
                    reader.nextChar();
                    break;
                default:
                    td.base = 10;
                    putChar('0');
                    break;
            }
            if(td.base != 10 && !isNumberSeparator(reader.ch) && digitToInt(reader.ch, td.base) < 0) {
                 error("invalid literal number");
            }
            getNumber();
        } else if(ch >= '1' && ch <= '9') {
            td.base = 10;
            getNumber();
        } else if(ch == '`') {
            getBackquotedIdent();
        } else if(ch == '\"') {
            Consumer<Boolean> stringPart = multiline -> {
                getStringPart(multiline);
                currentRegion = new InString(multiline, currentRegion);
            };
            Runnable fetchDoubleQuote = () -> {
                if(td.token == INTERPOLATIONID) {
                    reader.nextRawChar();
                    if(reader.ch == '\"') {
                        if(reader.lookaheadChar() == '\"') {
                            reader.nextRawChar();
                            reader.nextRawChar();
                            stringPart.accept(true);
                        } else {
                            reader.nextChar();
                            td.token = STRINGLIT;
                            td.strVal = "";
                        }
                    } else {
                        stringPart.accept(false);
                    }
                } else {
                    reader.nextChar();
                    if(reader.ch == '\"') {
                        reader.nextChar();
                        if(reader.ch == '\"') {
                            reader.nextRawChar();
                            getRawStringLit();
                        } else {
                            td.token = STRINGLIT;
                            td.strVal = "";
                        }
                    } else {
                        getStringLit();
                    }
                }
            };
            fetchDoubleQuote.run();
        } else if(ch == '\'') {
            reader.nextChar();
            if(isIdentifierStart(reader.ch)) {
                charLitOr(() -> { getIdentRest(); return QUOTEID; });
            } else if(isIdentifierPart(reader.ch) && reader.ch != '\\') {
                charLitOr(() -> { getOperatorRest(); return QUOTEID; });
            } else {
                switch(reader.ch) {
                    case '{':
                    case '[':
                    case ' ':
                    case '\t':
                        if(reader.lookaheadChar() != '\'') td.token = QUOTE;
                        break;
                    default:
                        if(reader.isAtEnd() && (reader.ch != SU && reader.ch != CR && reader.ch != LF || reader.isUnicodeEscape())) {
                            boolean isEmptyCharLit = reader.ch == '\'';
                            getLitChar();
                            if(reader.ch == '\'') {
                                if(isEmptyCharLit) {
                                     error("empty character literal (use '\\'' for single quote)");
                                } else {
                                    finishCharLit();
                                }
                            } else if(isEmptyCharLit) {
                                 error("empty character literal");
                            } else {
                                error("unclosed character literal");
                            }
                        } else {
                             error("unclosed character literal");
                        }
                }
            }
        } else if (ch == '.') {
            reader.nextChar();
            if (reader.ch >= '0' && reader.ch <= '9') {
                putChar('.');
                getFraction();
                setStrVal();
            } else {
                td.token = DOT;
            }
        } else if (ch == ';') {
            reader.nextChar();
            td.token = SEMI;
        } else if (ch == ',') {
            reader.nextChar();
            td.token = COMMA;
        } else if (ch == '(') {
            reader.nextChar();
            td.token = LPAREN;
        } else if (ch == '{') {
            reader.nextChar();
            td.token = LBRACE;
        } else if (ch == ')') {
            reader.nextChar();
            td.token = RPAREN;
        } else if (ch == '}') {
            if(inMultiLineInterpolatedExpression()) reader.nextRawChar();
            else reader.nextChar();
            td.token = RBRACE;
        } else if (ch == '[') {
            reader.nextChar();
            td.token = LBRACKET;
        } else if (ch == ']') {
            reader.nextChar();
            td.token = RBRACKET;
        } else if (ch == SU) {
            if(reader.isAtEnd()) td.token = EOF;
            else {
                 error("illegal character");
                reader.nextChar();
            }
        } else {
            if(ch == 0x21D2) {
                reader.nextChar();
                td.token = ARROW;
            } else if (ch == 0x2190) {
                reader.nextChar();
                td.token = LARROW;
            } else if (Character.isUnicodeIdentifierStart(ch)) {
                putChar(ch);
                reader.nextChar();
                getIdentRest();
            } else if (isSpecial(ch)) {
                putChar(ch);
                reader.nextChar();
                getOperatorRest();
            } else {
                // FIXME: Dotty deviation: f"" interpolator is not supported (#1814)
                 error(String.format("illegal character '\\u%04x'",reader.ch));
                reader.nextChar();
            }
        }
    }
    // Unsupported: Keeping comments
    private boolean skipComment() {
        Runnable skipLine = () -> {
            reader.nextChar();
            while(reader.ch != CR && reader.ch != LF && reader.ch != SU) {
                reader.nextChar();
            }
        };
        Runnable skipComment = () -> {
            int nested = 0;
            boolean flag = true;
            while(flag) {
                if(reader.ch == '/') {
                    reader.nextChar();
                    if(reader.ch == '*') {
                        nested += 1;
                        reader.nextChar();
                    }
                } else if(reader.ch == '*') {
                    reader.nextChar();
                    if(reader.ch == '/') {
                        if(nested > 0) {
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
        if(next.token == EMPTY) {
            lookAhead();
            reset();
        }
        return next;
    }

    public void skipParens(boolean multiple) {
        Tokens opening = td.token;
        nextToken();
        while(td.token != EOF && td.token.ordinal() != opening.ordinal() + 1) {
            if(td.token == opening && multiple) skipParens(true);
            else nextToken();
        }
        nextToken();
    }

    public boolean inModifierPosition() {
        Scanner lookahead = new LookaheadScanner();
        lookahead.nextToken();
        while(lookahead.td.isNewLine() || lookahead.isSoftModifier()) { lookahead.nextToken(); }
        return modifierFollowers.contains(lookahead.td.token);
    }

    public void getBackquotedIdent() {
        reader.nextChar();
        getLitChars('`');
        if(reader.ch == '`') {
            reader.nextChar();
            finishNamed(BACKQUOTED_IDENT, td);
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
        if(
                (reader.ch >= 'A' && reader.ch <= 'Z')
                || (reader.ch >= 'a' && reader.ch <= 'z')
                || (reader.ch >= '0' && reader.ch <= '9')
                || (reader.ch == '$')
        ) {
            putChar(reader.ch);
            reader.nextChar();
            getIdentRest();
        } else {
            switch(reader.ch) {
                case '_':
                    putChar(reader.ch);
                    reader.nextChar();
                    getIdentOrOperatorRest();
                    break;
                case SU:
                    finishNamed(IDENTIFIER, td);
                    break;
                default:
                    if(Character.isUnicodeIdentifierPart(reader.ch)) {
                        putChar(reader.ch);
                        reader.nextChar();
                        getIdentRest();
                    } else {
                        finishNamed(IDENTIFIER, td);
                    }
                    break;
            }
        }
    }

    public void getOperatorRest() {
        switch(reader.ch) {
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
            case '/':
                char nxch = reader.lookaheadChar();
                if(nxch == '/' || nxch == '*') finishNamed(IDENTIFIER, td);
                else {
                    putChar(reader.ch);
                    reader.nextChar();
                    getOperatorRest();
                }
                break;
            default:
                if(isSpecial(reader.ch)) {
                    putChar(reader.ch);
                    reader.nextChar();
                    getOperatorRest();
                } else finishNamed(IDENTIFIER, td);
                break;
        }
    }

    public void getIdentOrOperatorRest() {
        if(isIdentifierPart(reader.ch)) getIdentRest();
        else {
            switch(reader.ch) {
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
                    if(isSpecial(reader.ch)) {
                        getOperatorRest();
                    } else finishNamed(IDENTIFIER, td);
                    break;
            }
        }
    }

    public boolean isSoftModifier() {
        return td.token == IDENTIFIER
                && softModifierNames.contains(td.name);
    }

    public boolean isSoftModifierInModifierPosition() {
        return isSoftModifier() && inModifierPosition();
    }

    public boolean isSoftModifierInParamModifierPosition() {
        return isSoftModifier() && lookahead().token != COLON;
    }

    public boolean isErased() { return false; }

    public Set<Tokens> canStartStatTokens() {
        return canStartStatTokens3;
    }

    public Set<Tokens> canStartExprTokens() {
        return canStartExprTokens3;
    }

    public void getStringLit() {
        getLitChars('"');
        if(reader.ch == '"') {
            setStrVal();
            reader.nextChar();
            td.token = STRINGLIT;
        } else {
             error("unclosed string literal");
        }
    }

    public void getRawStringLit() {
        if(reader.ch == '"') {
            reader.nextRawChar();
            if (isTripleQuote()) {
                setStrVal();
                td.token = STRINGLIT;
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

    public void getStringPart(boolean multiLine) {
        if(reader.ch == '"') {
            if(multiLine) {
                reader.nextRawChar();
                if(isTripleQuote()) {
                    setStrVal();
                    td.token = STRINGLIT;
                } else getStringPart(multiLine);
            } else {
                reader.nextChar();
                setStrVal();
                td.token = STRINGLIT;
            }
        } else if (reader.ch == '\\' && !multiLine) {
            putChar(reader.ch);
            reader.nextRawChar();
            if (reader.ch == '"' || reader.ch == '\\') {
                putChar(reader.ch);
                reader.nextRawChar();
            }
            getStringPart(multiLine);
        } else if (reader.ch == '$') {
            reader.nextRawChar();
            if(reader.ch == '$' || reader.ch == '"') {
                putChar(reader.ch);
                reader.nextRawChar();
                getStringPart(multiLine);
            } else if (reader.ch == '{') {
                setStrVal();
                td.token = STRINGPART;
            } else if (Character.isUnicodeIdentifierStart(reader.ch) || reader.ch == '_') {
                setStrVal();
                td.token = STRINGPART;
                next.lastOffset = reader.charOffset - 1;
                next.offset = reader.charOffset - 1;
                putChar(reader.ch);
                reader.nextRawChar();
                while(reader.ch != SU && Character.isUnicodeIdentifierPart(reader.ch)) {
                    putChar(reader.ch);
                    reader.nextRawChar();
                }
                finishNamed(IDENTIFIER, next);
            } else {
                 error("invalid string interpolation: `$$`, `$\"`, `$`ident or `$`BlockExpr expected");
            }
        } else {
            boolean isUnclosedLiteral = !reader.isUnicodeEscape()
                    && (reader.ch == SU || (!multiLine && (reader.ch == CR || reader.ch == LF)));
            if(isUnclosedLiteral) {
                if(multiLine) {
                    incompleteInputError("unclosed multi-line string literal");
                } else {
                     error("unclosed string literal");
                }
            } else {
                putChar(reader.ch);
                reader.nextRawChar();
                getStringPart(multiLine);
            }
        }
    }

    public void fetchStringPart(boolean multiLine) {
        td.offset = reader.charOffset - 1;
        getStringPart(multiLine);
    }

    public boolean isTripleQuote() {
        if (reader.ch == '"') {
            reader.nextRawChar();
            if(reader.ch == '"') {
                reader.nextChar();
                while(reader.ch == '"') {
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
        Runnable invalidUnicodeEscape = () -> {
           error("invalid character in unicode escape sequence", reader.charOffset - 1);
            putChar(reader.ch);
        };
        Runnable putUnicode = () -> {
            while(reader.ch == 'u' || reader.ch == 'U') { reader.nextChar(); }
            int i = 0;
            int cp = 0;
            while (i < 4) {
                int shift = (3 - i) * 4;
                int d = digitToInt(reader.ch, 16);
                if(d < 0) {
                    invalidUnicodeEscape.run();
                    return;
                }
                cp += (d << shift);
                reader.nextChar();
                i += 1;
            }
            putChar((char)cp);
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
                 error(String.format("octal escape literals are unsupported: use %s instead", start));
                putChar((char)oct);
            } else if (reader.ch == 'u' || reader.ch == 'U') {
                putUnicode.run();
            } else {
                switch(reader.ch) {
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
        while(reader.ch != delimiter && !reader.isAtEnd() && (reader.ch != SU && reader.ch != CR && reader.ch != LF || reader.isUnicodeEscape())) {
            getLitChar();
        }
    }

    public void getFraction() {
        td.token = DECILIT;
        while('0' <= reader.ch && reader.ch <= '9' || isNumberSeparator(reader.ch)) {
            putChar(reader.ch);
            reader.nextChar();
        }
        checkNoTrailingSeparator();
        if(reader.ch == 'e' || reader.ch == 'E') {
            CustomCharArrayReader lookahead = reader.getLookaheadCharArrayReader();
            lookahead.nextChar();
            if (lookahead.ch == '+' || lookahead.ch == '-') {
                lookahead.nextChar();
            }
            if ('0' <= lookahead.ch && lookahead.ch <= '9' || isNumberSeparator(reader.ch)) {
                putChar(reader.ch);
                reader.nextChar();
                if (reader.ch == '+' || reader.ch == '-') {
                    putChar(reader.ch);
                    reader.nextChar();
                }
                while('0' <= reader.ch && reader.ch <= '9' || isNumberSeparator(reader.ch)) {
                    putChar(reader.ch);
                    reader.nextChar();
                }
                checkNoTrailingSeparator();
            }
            td.token = EXPOLIT;
        }
        if(reader.ch == 'd' || reader.ch == 'D') {
            putChar(reader.ch);
            reader.nextChar();
            td.token = DOUBLELIT;
        } else if(reader.ch == 'f' || reader.ch == 'F') {
            putChar(reader.ch);
            reader.nextChar();
            td.token = FLOATLIT;
        }
        checkNoLetter();
    }

    public void checkNoLetter() {
        if (isIdentifierPart(reader.ch) && reader.ch >= ' ') {
             error("Invalid literal number");
        }
    }

    public void getNumber() {
        while(isNumberSeparator(reader.ch) || digitToInt(reader.ch, td.base) >= 0) {
            putChar(reader.ch);
            reader.nextChar();
        }
        checkNoTrailingSeparator();
        td.token = INTLIT;
        if (td.base == 10 && reader.ch == '.') {
            char lch = reader.lookaheadChar();
            if('0' <= lch && lch <= '9') {
                putChar('.');
                reader.nextChar();
                getFraction();
            }
        } else {
            switch(reader.ch) {
                case 'e':
                case 'E':
                case 'f':
                case 'F':
                case 'd':
                case 'D':
                    if (td.base == 10) getFraction();
                    break;
                case 'l':
                case 'L':
                    reader.nextChar();
                    td.token = LONGLIT;
                    break;
                default:
                    break;
            }
        }
        checkNoTrailingSeparator();
        setStrVal();
    }

    public void finishCharLit() {
        reader.nextChar();
        td.token = CHARLIT;
        setStrVal();
    }

    public void charLitOr(Supplier<Tokens> op) {
        putChar(reader.ch);
        reader.nextChar();
        if(reader.ch == '\'') finishCharLit();
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
        switch(td.token) {
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
