package com.virtuslab.using_directives.custom.utils;

import com.virtuslab.using_directives.custom.Tokens;
import java.util.*;
import java.util.stream.Collectors;

public class TokenUtils {
  private static Collection<Tokens> tokenRange(Tokens start, Tokens end) {
    return Arrays.stream(Tokens.values())
        .filter(t -> t.ordinal() >= start.ordinal() && t.ordinal() <= end.ordinal())
        .collect(Collectors.toList());
  }

  private static Set<Tokens> identifierTokens() {
    Set<Tokens> set = new HashSet<>();
    set.add(Tokens.IDENTIFIER);
    set.add(Tokens.BACKQUOTED_IDENT);
    return set;
  }

  private static Map<Integer, Tokens> tokensMap =
      Arrays.stream(Tokens.values()).collect(Collectors.toMap(Enum::ordinal, t -> t));

  public static Tokens tokenFromInt(int value) {
    return tokensMap.get(value);
  }

  public static boolean isIdentifier(Tokens token) {
    return token.ordinal() >= Tokens.IDENTIFIER.ordinal()
        && token.ordinal() <= Tokens.BACKQUOTED_IDENT.ordinal();
  }

  public static boolean isValidUsingDirectiveStart(Tokens token) {
    return token == Tokens.USING;
  }

  public static Set<Tokens> alphaKeywords = new HashSet<>(tokenRange(Tokens.USING, Tokens.END));

  public static Set<Tokens> symbolicKeywords =
      new HashSet<>(tokenRange(Tokens.USCORE, Tokens.CTXARROW));

  public static Set<Tokens> identifierTokens = identifierTokens();

  public static Set<Tokens> keywords = keywords();

  public static Set<Tokens> literalTokens = literalTokens();

  public static Set<Tokens> openParensTokens = openParensTokens();

  public static Set<Tokens> canStartExprTokens3 = canStartExprTokens3();

  public static Set<Tokens> canEndStatTokens = canEndStatTokens();

  public static Set<Tokens> closingRegionTokens = closingRegionTokens();

  public static Set<Tokens> canStartIndentTokens = canStartIndentTokens();

  public static Set<Tokens> canStartStatTokens3 = canStartStatTokens3();

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
    set.add(Tokens.QUOTEID);
    set.add(Tokens.NULL);
    return set;
  }

  private static Set<Tokens> atomicExprTokens() {
    Set<Tokens> set = new HashSet<>(literalTokens());
    //    set.addAll(identifierTokens());
    set.add(Tokens.USCORE);
    set.add(Tokens.NULL);
    set.add(Tokens.TRUE);
    set.add(Tokens.FALSE);
    set.add(Tokens.QUOTEID);
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
    return set;
  }

  private static Set<Tokens> canEndStatTokens() {
    Set<Tokens> set = new HashSet<>(atomicExprTokens());
    set.add(Tokens.RPAREN);
    set.add(Tokens.RBRACE);
    set.add(Tokens.RBRACKET);
    set.add(Tokens.OUTDENT);
    return set;
  }

  private static Set<Tokens> closingRegionTokens() {
    Set<Tokens> set = new HashSet<>();
    set.add(Tokens.RBRACE);
    set.add(Tokens.RPAREN);
    set.add(Tokens.RBRACKET);
    return set;
  }

  private static Set<Tokens> canStartIndentTokens() {
    Set<Tokens> set = new HashSet<>();
    set.add(Tokens.USING);
    set.add(Tokens.COLONEOL);
    set.add(Tokens.EQUALS);
    set.add(Tokens.CTXARROW);
    return set;
  }

  private static Set<Tokens> canStartStatTokens3() {
    Set<Tokens> set = new HashSet<>(canStartExprTokens3());
    set.add(Tokens.USING);
    set.add(Tokens.AT);
    set.add(Tokens.END);
    return set;
  }
}
