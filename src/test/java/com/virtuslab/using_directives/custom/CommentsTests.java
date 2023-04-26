package com.virtuslab.using_directives.custom;

import static com.virtuslab.using_directives.TestUtils.testCode;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.virtuslab.using_directives.DirectiveAssertions;
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
    testCode(1, plainComment, specialComment, keywordDirective);
    testCode(1, specialComment, plainComment);
    testCode(2, specialComment, specialComment2);
    testCode(1, multiLine1);
    testCode(1, multiLine2);
    testCode(1, multiLine1, multiLine2);
    testCode(1, keywordDirective, specialComment, plainComment);
    testCode(1, binaryScalaVersionNumericComment);
    testCode(2, numericScalaVersionDirective, binaryScalaVersionNumericComment);

    testCode(0, plainComment);
    testCode(0, keywordDirective);
    testCode(1, noValueDirective);
  }

  @Test
  public void testKeywordDirectives() {
    testCode(0, keywordDirective, keywordDirective2);
    testCode(0, keywordDirective2);
  }

  @Test
  public void testPlainComments() {
    testCode(1, plainComment, specialComment);
    testCode(0, plainComment, plainComment2);
    testCode(1, specialComment, plainComment);

    testCode(1, specialComment);
    testCode(0, keywordDirective);

    testCode(1, keywordDirective, specialComment, plainComment);
  }

  @Test
  public void testKeywordAfterComment() {
    testCode(1, plainComment, specialComment, keywordDirective);
  }

  @Test
  public void testMalformedKeyword() {
    String malformedDirective = "> using malformedKeywordDirective \"malformedValue1\"";
    String malformedDirective2 = "! using malformedKeywordDirective2 \"malformedValue2\"";
    String malformedDirective3 = ">>> using malformedKeywordDirective3 \"malformedValue3\"";
    String malformedDirective4 = ": using malformedKeywordDirective4 \"malformedValue4\"";

    testCode(0, malformedDirective, specialComment);
    testCode(0, malformedDirective);
    testCode(0, malformedDirective2);
    testCode(0, malformedDirective3);
    testCode(0, malformedDirective4);

    testCode(1, specialComment, malformedDirective);
    testCode(1, specialComment, malformedDirective2);
    testCode(1, specialComment, malformedDirective3);
  }

  @Test
  public void testLines() {
    UsingDirectives d1 =
        testCode(
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

    UsingDirectives d2 = testCode(1, "", specialComment2);
    testLines(d2, 1);
    UsingDirectives d3 = testCode(2, specialComment2, specialComment);
    testLines(d3, 0, 1);
  }

  @Test
  public void testIndexes() {
    String code1 = "//> using javaProp \"foo1\"\n  //> using javaProp2 \"foo2=bar2\"";
    DirectiveAssertions.assertPositions(code1, new int[] {0, 19}, new int[] {1, 22});
  }
}
