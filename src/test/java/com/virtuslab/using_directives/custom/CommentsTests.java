package com.virtuslab.using_directives.custom;

import static com.virtuslab.using_directives.TestUtils.testCode;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.virtuslab.using_directives.DirectiveAssertions;
import com.virtuslab.using_directives.custom.model.UsingDirectiveKind;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class CommentsTests {

  String plainComment =
      "// using plainComment0123 \"pcValue\", \"pcValue1\", \"pcValue2\", \"pcValue3\"";
  String plainComment2 = "// using plainComment4 \"pcValue4\"";
  String specialComment = "//> using specialComment \"scValue\", \"scValue3\", \"scValue4\"";
  String specialComment2 = "//> using specialComment2 \"scValue2\"";
  String multiLine1 =
      "//> using multiLineKey\n"
          + "//>   \"firstLine\", \n"
          + "//>   \"secondLine\", \n"
          + "//>   \"thirdLine\"";
  String multiLine2 =
      "//> using multiLineKey2:\n"
          + "//>   setting1 \"firstLine\", \n"
          + "//>   setting2 \"secondLine\", \n"
          + "//>   setting3 \"thirdLine\"";
  String keywordDirective = "using keywordDirective \"kdValue\"";
  String keywordDirective2 = "using keywordDirective2 kdValue2";
  String numericScalaVersionDirective = "//> using scalaFullNumeric 2.4.15 ";
  String binaryScalaVersionNumericComment = "//> using scalaBinary 2.1";
  String noValueDirective = "//> using noValueKey";

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
    testCode(UsingDirectiveKind.SpecialComment, 1, plainComment, specialComment, keywordDirective);
    testCode(UsingDirectiveKind.SpecialComment, 1, specialComment, plainComment);
    testCode(UsingDirectiveKind.SpecialComment, 2, specialComment, specialComment2);
    testCode(UsingDirectiveKind.SpecialComment, 1, multiLine1);
    testCode(UsingDirectiveKind.SpecialComment, 1, multiLine2);
    testCode(UsingDirectiveKind.SpecialComment, 2, multiLine1, multiLine2);
    testCode(UsingDirectiveKind.SpecialComment, 1, keywordDirective, specialComment, plainComment);
    testCode(UsingDirectiveKind.SpecialComment, 1, binaryScalaVersionNumericComment);
    testCode(
        UsingDirectiveKind.SpecialComment,
        1,
        numericScalaVersionDirective,
        binaryScalaVersionNumericComment);

    testCode(UsingDirectiveKind.SpecialComment, 0, plainComment);
    testCode(UsingDirectiveKind.SpecialComment, 0, keywordDirective);
    testCode(UsingDirectiveKind.SpecialComment, 1, noValueDirective);
  }

  @Test
  public void testKeywordDirectives() {
    testCode(UsingDirectiveKind.Code, 1, keywordDirective, keywordDirective2);
    testCode(UsingDirectiveKind.Code, 0, keywordDirective2);
  }

  @Test
  public void testPlainComments() {
    testCode(UsingDirectiveKind.PlainComment, 1, plainComment, specialComment);
    testCode(UsingDirectiveKind.PlainComment, 2, plainComment, plainComment2);
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
    String malformedDirective = "> using malformedKeywordDirective \"malformedValue1\"";
    String malformedDirective2 = "! using malformedKeywordDirective2 \"malformedValue2\"";
    String malformedDirective3 = ">>> using malformedKeywordDirective3 \"malformedValue3\"";
    String malformedDirective4 = ": using malformedKeywordDirective4 \"malformedValue4\"";

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

  @Test
  public void testIndexes() {
    String code1 = "// using javaProp \"foo1\"\n  // using javaProp2 \"foo2=bar2\"";
    DirectiveAssertions.assertPositions(
        UsingDirectiveKind.PlainComment, code1, new int[] {0, 18}, new int[] {1, 21});
  }
}
