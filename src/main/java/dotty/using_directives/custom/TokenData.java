package dotty.using_directives.custom;

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

    public boolean isIdent() {
        return token == Tokens.IDENTIFIER || token == Tokens.BACKQUOTED_IDENT;
    }

    public boolean isIdent(String name) {
        return token == Tokens.IDENTIFIER && this.name.equals(name);
    }

    public boolean isNestedStart() {
        return token == Tokens.LBRACE || token == Tokens.INDENT;
    }

    public boolean isNestedEnd() {
        return token == Tokens.RBRACE || token == Tokens.OUTDENT;
    }

    public boolean isColon() {
        if(token == Tokens.COLONEOL) {
            token = Tokens.COLON;
        }
        return token == Tokens.COLON;
    }

    public boolean isAfterLineEnd() {
        return lineOffset >= 0;
    }

    // NotImplemented
    public boolean isOperator() {
        return false;
    }

    public boolean isArrow() {
        return token == Tokens.ARROW || token == Tokens.CTXARROW;
    }
}
