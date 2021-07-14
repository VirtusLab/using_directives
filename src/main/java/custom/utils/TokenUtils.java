package dotty.using_directives.custom.utils;

import dotty.using_directives.custom.Tokens;

import java.util.*;
import java.util.stream.Collectors;

import static dotty.using_directives.custom.Tokens.*;

public class TokenUtils {
    private static Collection<Tokens> tokenRange(Tokens start, Tokens end) {
        return  Arrays.stream(Tokens.values())
                .filter(t -> t.ordinal() >= start.ordinal() && t.ordinal() <= end.ordinal())
                .collect(Collectors.toList());
    }

    private static Set<Tokens> identifierTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(IDENTIFIER);
        set.add(BACKQUOTED_IDENT);
        return set;
    }

    public static boolean isIdentifier(Tokens token) {
        return token.ordinal() >= IDENTIFIER.ordinal() && token.ordinal() <= BACKQUOTED_IDENT.ordinal();
    }

    public static Set<Tokens> alphaKeywords = new HashSet<>(tokenRange(IF, END));

    public static Set<Tokens> symbolicKeywords = new HashSet<>(tokenRange(USCORE, CTXARROW));

    public static Set<Tokens> identifierTokens = identifierTokens();

    public static Set<Tokens> keywords = keywords();

    public static Set<Tokens> simpleLiteralTokens = simpleLiteralTokens();

    public static Set<Tokens> literalTokens = literalTokens();

    public static Set<Tokens> atomicExprTokens = atomicExprTokens();

    public static Set<Tokens> openParensTokens = openParensTokens();

    public static Set<Tokens> canStartExprTokens3 = canStartExprTokens3();

    public static Set<Tokens> canStartTypeTokens = canStartTypeTokens();

    public static Set<Tokens> canEndStatTokens = canEndStatTokens();

    public static Set<Tokens> templateIntroTokens = templateIntroTokens();

    public static Set<Tokens> localModifierTokens = localModifierTokens();

    public static Set<Tokens> statCtdTokens = statCtdTokens();

    public static Set<Tokens> closingRegionTokens = closingRegionTokens();

    public static Set<Tokens> canStartIndentTokens = canStartIndentTokens();

    public static Set<Tokens> accessModifierTokens = accessModifierTokens();

    public static Set<Tokens> modifierTokensOrCase = modifierTokensOrCase();

    public static Set<Tokens> modifierFollowers = modifierFollowers();

    public static Set<Tokens> endMarkerTokens = endMarkerTokens();

    public static Set<Tokens> modifierTokens = modifierTokens();

    public static Set<Tokens> dclIntroTokens = dclIntroTokens();

    public static Set<Tokens> defIntroTokens = defIntroTokens();

    public static Set<Tokens> mustStartStatTokens = mustStartStatTokens();

    public static Set<Tokens> canStartStatTokens3 = canStartStatTokens3();

    public static Set<String> softModifierNames = softModifierNames();

    public static Map<String, Tokens> keywordMap = keywordMap();

    public static Map<String, Tokens> keywordMap() {
        Map<String, Tokens> map = new HashMap<>();
        keywords().forEach(t -> map.put(t.str, t));
        return map;
    }


    private static Set<Tokens> keywords() {
        Set<Tokens> set = new HashSet<>();
        set.addAll(alphaKeywords);
        set.addAll(symbolicKeywords);
        return set;
    }


    private static Set<Tokens> simpleLiteralTokens() {
        Set<Tokens> set = new HashSet<>(tokenRange(CHARLIT, STRINGLIT));
        set.add(TRUE);
        set.add(FALSE);
        return set;
    }

    private static Set<Tokens> literalTokens() {
        Set<Tokens> set = new HashSet<>(simpleLiteralTokens());
        set.add(INTERPOLATIONID);
        set.add(QUOTEID);
        set.add(NULL);
        return set;
    }

    private static Set<Tokens> atomicExprTokens() {
        Set<Tokens> set = new HashSet<>(literalTokens());
        set.addAll(identifierTokens());
        set.add(USCORE);
        set.add(NULL);
        set.add(THIS);
        set.add(SUPER);
        set.add(TRUE);
        set.add(FALSE);
        set.add(RETURN);
        set.add(QUOTEID);
        set.add(XMLSTART);
        return set;
    }


    private static Set<Tokens> openParensTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(LBRACE);
        set.add(LPAREN);
        set.add(LBRACKET);
        return set;
    }


    private static Set<Tokens> canStartExprTokens3() {
        Set<Tokens> set = new HashSet<>(atomicExprTokens());
        set.addAll(openParensTokens());
        set.add(INDENT);
        set.add(QUOTE);
        set.add(IF);
        set.add(WHILE);
        set.add(FOR);
        set.add(NEW);
        set.add(TRY);
        set.add(THROW);
        return set;
    }


    private static Set<Tokens> canStartTypeTokens() {
        Set<Tokens> set = new HashSet<>(literalTokens());
        set.addAll(identifierTokens());
        set.add(THIS);
        set.add(SUPER);
        set.add(USCORE);
        set.add(LPAREN);
        set.add(AT);
        return set;
    }


    private static Set<Tokens> canEndStatTokens() {
        Set<Tokens> set = new HashSet<>(atomicExprTokens());
        set.add(TYPE);
        set.add(GIVEN);
        set.add(RPAREN);
        set.add(RBRACE);
        set.add(RBRACKET);
        set.add(OUTDENT);
        return set;
    }


    private static Set<Tokens> templateIntroTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(CLASS);
        set.add(TRAIT);
        set.add(OBJECT);
        set.add(ENUM);
        set.add(CASECLASS);
        set.add(CASEOBJECT);
        return set;
    }


    private static Set<Tokens> localModifierTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(ABSTRACT);
        set.add(FINAL);
        set.add(SEALED);
        set.add(LAZY);
        return set;
    }

    private static Set<Tokens> statCtdTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(THEN);
        set.add(ELSE);
        set.add(DO);
        set.add(CATCH);
        set.add(FINALLY);
        set.add(YIELD);
        set.add(MATCH);
        return set;
    }


    private static Set<Tokens> closingRegionTokens() {
        Set<Tokens> set = new HashSet<>(statCtdTokens());
        set.add(RBRACE);
        set.add(RPAREN);
        set.add(RBRACKET);
        set.add(CASE);
        return set;
    }

    private static Set<Tokens> canStartIndentTokens() {
        Set<Tokens> set = new HashSet<>(statCtdTokens());
//        set.addAll(identifierTokens);
        set.add(COLONEOL);
        set.add(WITH);
        set.add(EQUALS);
        set.add(ARROW);
        set.add(CTXARROW);
        set.add(LARROW);
        set.add(WHILE);
        set.add(TRY);
        set.add(FOR);
        set.add(IF);
        set.add(THROW);
        set.add(RETURN);
        return set;
    }


    private static Set<Tokens> accessModifierTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(PRIVATE);
        set.add(PROTECTED);
        return set;
    }



    private static Set<Tokens> modifierTokensOrCase() {
        Set<Tokens> set = new HashSet<>(modifierTokens());
        set.add(CASE);
        return set;
    }

    private static Set<Tokens> modifierFollowers() {
        Set<Tokens> set = new HashSet<>(modifierTokensOrCase());
        set.addAll(defIntroTokens());
        return set;
    }



    private static Set<Tokens> endMarkerTokens() {
        Set<Tokens> set = new HashSet<>(identifierTokens());
        set.add(IF);
        set.add(WHILE);
        set.add(FOR);
        set.add(MATCH);
        set.add(TRY);
        set.add(NEW);
        set.add(THROW);
        set.add(GIVEN);
        set.add(VAL);
        set.add(THIS);
        return set;
    }

    private static Set<Tokens> modifierTokens() {
        Set<Tokens> set = new HashSet<>(localModifierTokens());
        set.addAll(accessModifierTokens());
        set.add(OVERRIDE);
        return set;
    }


    private static Set<Tokens> dclIntroTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(DEF);
        set.add(VAL);
        set.add(VAR);
        set.add(TYPE);
        set.add(GIVEN);
        return set;
    }

    private static Set<Tokens> defIntroTokens() {
        Set<Tokens> set = new HashSet<>(templateIntroTokens());
        set.addAll(dclIntroTokens());
        return set;
    }



    private static Set<Tokens> mustStartStatTokens() {
        Set<Tokens> set = new HashSet<>(defIntroTokens());
        set.addAll(modifierTokens());
        set.add(IMPORT);
        set.add(EXPORT);
        set.add(PACKAGE);
        return set;
    }


    private static Set<Tokens> canStartStatTokens3() {
        Set<Tokens> set = new HashSet<>(canStartExprTokens3());
        set.addAll(mustStartStatTokens());
        set.add(AT);
        set.add(CASE);
        set.add(END);
        return set;
    }

    public static Set<String> softModifierNames() {
        Set<String> set = new HashSet<>();
        set.add("inline");
        set.add("opaque");
        set.add("open");
        set.add("transparent");
        set.add("infix");

        return set;
    }

}
