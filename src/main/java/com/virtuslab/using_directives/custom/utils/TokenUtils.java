package com.virtuslab.using_directives.custom.utils;

import com.virtuslab.using_directives.custom.Tokens;

import java.util.*;
import java.util.stream.Collectors;

public class TokenUtils {
    private static Collection<Tokens> tokenRange(Tokens start, Tokens end) {
        return  Arrays.stream(Tokens.values())
                .filter(t -> t.ordinal() >= start.ordinal() && t.ordinal() <= end.ordinal())
                .collect(Collectors.toList());
    }

    private static Set<Tokens> identifierTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(Tokens.IDENTIFIER);
        set.add(Tokens.BACKQUOTED_IDENT);
        return set;
    }

    private static Map<Integer, Tokens> tokensMap = Arrays.stream(Tokens.values()).collect(Collectors.toMap(Enum::ordinal, t -> t));

    public static Tokens tokenFromInt(int value) {
        return tokensMap.get(value);
    }

    public static boolean isIdentifier(Tokens token) {
        return token.ordinal() >= Tokens.IDENTIFIER.ordinal() && token.ordinal() <= Tokens.BACKQUOTED_IDENT.ordinal();
    }

    public static Set<Tokens> alphaKeywords = new HashSet<>(tokenRange(Tokens.IF, Tokens.END));

    public static Set<Tokens> symbolicKeywords = new HashSet<>(tokenRange(Tokens.USCORE, Tokens.CTXARROW));

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
        Set<Tokens> set = new HashSet<>(tokenRange(Tokens.CHARLIT, Tokens.STRINGLIT));
        set.add(Tokens.TRUE);
        set.add(Tokens.FALSE);
        return set;
    }

    private static Set<Tokens> literalTokens() {
        Set<Tokens> set = new HashSet<>(simpleLiteralTokens());
        set.add(Tokens.INTERPOLATIONID);
        set.add(Tokens.QUOTEID);
        set.add(Tokens.NULL);
        return set;
    }

    private static Set<Tokens> atomicExprTokens() {
        Set<Tokens> set = new HashSet<>(literalTokens());
        set.addAll(identifierTokens());
        set.add(Tokens.USCORE);
        set.add(Tokens.NULL);
        set.add(Tokens.THIS);
        set.add(Tokens.SUPER);
        set.add(Tokens.TRUE);
        set.add(Tokens.FALSE);
        set.add(Tokens.RETURN);
        set.add(Tokens.QUOTEID);
        set.add(Tokens.XMLSTART);
        return set;
    }


    private static Set<Tokens> openParensTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(Tokens.LBRACE);
        set.add(Tokens.LPAREN);
        set.add(Tokens.LBRACKET);
        return set;
    }


    private static Set<Tokens> canStartExprTokens3() {
        Set<Tokens> set = new HashSet<>(atomicExprTokens());
        set.addAll(openParensTokens());
        set.add(Tokens.INDENT);
        set.add(Tokens.QUOTE);
        set.add(Tokens.IF);
        set.add(Tokens.WHILE);
        set.add(Tokens.FOR);
        set.add(Tokens.NEW);
        set.add(Tokens.TRY);
        set.add(Tokens.THROW);
        return set;
    }


    private static Set<Tokens> canStartTypeTokens() {
        Set<Tokens> set = new HashSet<>(literalTokens());
        set.addAll(identifierTokens());
        set.add(Tokens.THIS);
        set.add(Tokens.SUPER);
        set.add(Tokens.USCORE);
        set.add(Tokens.LPAREN);
        set.add(Tokens.AT);
        return set;
    }


    private static Set<Tokens> canEndStatTokens() {
        Set<Tokens> set = new HashSet<>(atomicExprTokens());
        set.add(Tokens.TYPE);
        set.add(Tokens.GIVEN);
        set.add(Tokens.RPAREN);
        set.add(Tokens.RBRACE);
        set.add(Tokens.RBRACKET);
        set.add(Tokens.OUTDENT);
        return set;
    }


    private static Set<Tokens> templateIntroTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(Tokens.CLASS);
        set.add(Tokens.TRAIT);
        set.add(Tokens.OBJECT);
        set.add(Tokens.ENUM);
        set.add(Tokens.CASECLASS);
        set.add(Tokens.CASEOBJECT);
        return set;
    }


    private static Set<Tokens> localModifierTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(Tokens.ABSTRACT);
        set.add(Tokens.FINAL);
        set.add(Tokens.SEALED);
        set.add(Tokens.LAZY);
        return set;
    }

    private static Set<Tokens> statCtdTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(Tokens.THEN);
        set.add(Tokens.ELSE);
        set.add(Tokens.DO);
        set.add(Tokens.CATCH);
        set.add(Tokens.FINALLY);
        set.add(Tokens.YIELD);
        set.add(Tokens.MATCH);
        return set;
    }


    private static Set<Tokens> closingRegionTokens() {
        Set<Tokens> set = new HashSet<>(statCtdTokens());
        set.add(Tokens.RBRACE);
        set.add(Tokens.RPAREN);
        set.add(Tokens.RBRACKET);
        set.add(Tokens.CASE);
        return set;
    }

    private static Set<Tokens> canStartIndentTokens() {
        Set<Tokens> set = new HashSet<>(statCtdTokens());
//        set.addAll(identifierTokens);
        set.add(Tokens.ATUSING);
        set.add(Tokens.COLONEOL);
        set.add(Tokens.WITH);
        set.add(Tokens.EQUALS);
        set.add(Tokens.ARROW);
        set.add(Tokens.CTXARROW);
        set.add(Tokens.LARROW);
        set.add(Tokens.WHILE);
        set.add(Tokens.TRY);
        set.add(Tokens.FOR);
        set.add(Tokens.IF);
        set.add(Tokens.THROW);
        set.add(Tokens.RETURN);
        return set;
    }


    private static Set<Tokens> accessModifierTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(Tokens.PRIVATE);
        set.add(Tokens.PROTECTED);
        return set;
    }



    private static Set<Tokens> modifierTokensOrCase() {
        Set<Tokens> set = new HashSet<>(modifierTokens());
        set.add(Tokens.CASE);
        return set;
    }

    private static Set<Tokens> modifierFollowers() {
        Set<Tokens> set = new HashSet<>(modifierTokensOrCase());
        set.addAll(defIntroTokens());
        return set;
    }



    private static Set<Tokens> endMarkerTokens() {
        Set<Tokens> set = new HashSet<>(identifierTokens());
        set.add(Tokens.IF);
        set.add(Tokens.WHILE);
        set.add(Tokens.FOR);
        set.add(Tokens.MATCH);
        set.add(Tokens.TRY);
        set.add(Tokens.NEW);
        set.add(Tokens.THROW);
        set.add(Tokens.GIVEN);
        set.add(Tokens.VAL);
        set.add(Tokens.THIS);
        return set;
    }

    private static Set<Tokens> modifierTokens() {
        Set<Tokens> set = new HashSet<>(localModifierTokens());
        set.addAll(accessModifierTokens());
        set.add(Tokens.OVERRIDE);
        return set;
    }


    private static Set<Tokens> dclIntroTokens() {
        Set<Tokens> set = new HashSet<>();
        set.add(Tokens.DEF);
        set.add(Tokens.VAL);
        set.add(Tokens.VAR);
        set.add(Tokens.TYPE);
        set.add(Tokens.GIVEN);
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
        set.add(Tokens.IMPORT);
        set.add(Tokens.EXPORT);
        set.add(Tokens.PACKAGE);
        return set;
    }


    private static Set<Tokens> canStartStatTokens3() {
        Set<Tokens> set = new HashSet<>(canStartExprTokens3());
        set.addAll(mustStartStatTokens());
        set.add(Tokens.AT);
        set.add(Tokens.CASE);
        set.add(Tokens.END);
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
