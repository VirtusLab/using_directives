package com.virtuslab.using_directives.custom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.virtuslab.using_directives.UsingDirectivesProcessor;
import com.virtuslab.using_directives.custom.model.UsingDirectiveKind;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class CommentsTests {

  String plainComment = "// using plainComment \"b\"";
  String specialComment = "//> using specialComment \"b\"";
  String specialComment2 = "//> using specialComment2 \"b\"";
  String keywordDirective = "using keywordDirective \"b\"";

  private UsingDirectives testCode(
      UsingDirectiveKind expectedKind, int expectedCount, String... examples) {
    String code = Arrays.stream(examples).collect(Collectors.joining("\n")) + "\n// code...";
    UsingDirectivesProcessor processor = new UsingDirectivesProcessor();

    UsingDirectives directives =
        processor
            .extract(
                code.toCharArray(),
                expectedKind == UsingDirectiveKind.SpecialComment,
                expectedKind == UsingDirectiveKind.PlainComment)
            .stream()
            .filter(d -> d.getKind() == expectedKind)
            .findFirst()
            .get();

    assertEquals(expectedCount, directives.getFlattenedMap().size());
    assertEquals(expectedKind, directives.getKind());
    return directives;
  }

  private void testLines(UsingDirectives usingDirectives, int... expected) {
    Set<Integer> lines =
        usingDirectives.getFlattenedMap().values().stream()
            .flatMap(u -> u.stream())
            .map(d -> d.getRelatedASTNode().getPosition().getLine())
            .collect(Collectors.toSet());

    Arrays.stream(expected)
        .forEach(
            line ->
                assertTrue(
                    lines.contains(line),
                    "No using directive in line " + line + " in " + usingDirectives));
  }

  @Test
  public void testSpecialComments() {
    //        testCode(UsingDirectiveKind.SpecialComment, 1, plainComment, specialComment);
    //        testCode(UsingDirectiveKind.SpecialComment, 1, specialComment, plainComment);
    testCode(UsingDirectiveKind.SpecialComment, 1, keywordDirective, specialComment, plainComment);

    testCode(UsingDirectiveKind.SpecialComment, 0, plainComment);
    testCode(UsingDirectiveKind.SpecialComment, 0, keywordDirective);
  }

  @Test
  public void testPlainComments() {
    // testCode(UsingDirectiveKind.PlainComment, 1, plainComment, specialComment);
    testCode(UsingDirectiveKind.PlainComment, 1, specialComment, plainComment);

    testCode(UsingDirectiveKind.PlainComment, 0, specialComment);
    testCode(UsingDirectiveKind.PlainComment, 0, keywordDirective);

    testCode(UsingDirectiveKind.PlainComment, 1, keywordDirective, specialComment, plainComment);
  }

  @Test
  public void testKeywordAfterComment() {
    testCode(UsingDirectiveKind.Code, 1, plainComment, specialComment, keywordDirective);
  }

  @Test
  public void testMalformedKeyword() {
    String malformedDirective = "> using malformedKeywordDirective \"b\"";
    String malformedDirective2 = "! using malformedKeywordDirective2 \"b\"";
    String malformedDirective3 = ">>> using malformedKeywordDirective3 \"b\"";
    String malformedDirective4 = ": using malformedKeywordDirective3 \"b\"";

    testCode(UsingDirectiveKind.Code, 0, malformedDirective, keywordDirective);
    testCode(UsingDirectiveKind.Code, 0, malformedDirective);
    testCode(UsingDirectiveKind.Code, 0, malformedDirective2);
    testCode(UsingDirectiveKind.Code, 0, malformedDirective3);
    testCode(UsingDirectiveKind.Code, 0, malformedDirective4);

    testCode(UsingDirectiveKind.Code, 1, keywordDirective, malformedDirective);
    testCode(UsingDirectiveKind.Code, 1, keywordDirective, malformedDirective2);
    testCode(UsingDirectiveKind.Code, 1, keywordDirective, malformedDirective3);
  }

  @Test
  public void testLines() {
    UsingDirectives d1 =
        testCode(
            UsingDirectiveKind.SpecialComment,
            2,
            "",
            "// ala",
            keywordDirective,
            specialComment,
            "",
            keywordDirective,
            "",
            specialComment2,
            "");
    testLines(d1, 3, 7);

    UsingDirectives d2 = testCode(UsingDirectiveKind.SpecialComment, 1, "", specialComment2);
    testLines(d2, 1);
    UsingDirectives d3 =
        testCode(UsingDirectiveKind.SpecialComment, 2, specialComment2, specialComment);
    testLines(d3, 0, 1);
  }
}
