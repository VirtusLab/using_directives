package dotty.using_directives.custom;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dotty.using_directives.custom.utils.TokenUtils.*;

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
    IF("if"),
    FOR("for"),
    ELSE("else"),
    THIS("this"),
    NULL("null"),
    NEW("new"),
    SUPER("super"),
    ABSTRACT("abstract"),
    FINAL("final"),
    PRIVATE("private"),
    PROTECTED("protected"),
    OVERRIDE("override"),
    EXTENDS("extends"),
    TRUE("true"),
    FALSE("false"),
    CLASS("class"),
    IMPORT("import"),
    PACKAGE("package"),
    DO("do"),
    THROW("throw"),
    TRY("try"),
    CATCH("catch"),
    FINALLY("finally"),
    WHILE("while"),
    RETURN("return"),
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
    WITH("with"),
    CASE("case"),
    CASECLASS("case class"),
    CASEOBJECT("case object"),
    VAL("val"),
    IMPLICIT("implicit"),
    VAR("var"),
    DEF("def"),
    TYPE("type"),
    OBJECT("object"),
    YIELD("yield"),
    TRAIT("trait"),
    SEALED("sealed"),
    MATCH("match"),
    LAZY("lazy"),
    THEN("then"),
    FORSOME("forSome"),
    ENUM("enum"),
    GIVEN("given"),
    EXPORT("export"),
    MACRO("macro"),
    END("end"),
    NEWLINE("end of statement", "new line"),
    NEWLINES("end of statement", "new lines"),
    USCORE("_"),
    LARROW("<-"),
    ARROW("=>"),
    SUBTYPE("<:"),
    SUPERTYPE(">:"),
    HASH("#"),
    VIEWBOUND("<%"),
    TLARROW("=>>"),
    CTXARROW("?=>"),
    QUOTE("'"),
    COLONEOL(":", ": at eol"),
    SELFARROW("=>"),
    XMLSTART("$XMLSTART$<");

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
