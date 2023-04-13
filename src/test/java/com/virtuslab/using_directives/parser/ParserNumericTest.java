package com.virtuslab.using_directives.parser;

import static com.virtuslab.using_directives.DirectiveAssertions.assertDiagnostic;
import static com.virtuslab.using_directives.DirectiveAssertions.assertNumericValueAtPath;
import static com.virtuslab.using_directives.TestUtils.*;

import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.reporter.PersistentReporter;
import org.junit.jupiter.api.Test;

public class ParserNumericTest {

  @Test
  public void testNumeric() {
    String input = "using keyA 12";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "12");
  }

  @Test
  public void testNumericFraction() {
    String input = "using keyA 1.2";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "1.2");
  }

  @Test
  public void testShortNumericFraction() {
    String input = "using keyA .2";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", ".2");
  }

  @Test
  public void testLongLiteral() {
    String input = "using keyA 9000000000L";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "9000000000L");
  }

  @Test
  public void testHexNumeric() {
    String input = "using keyA 0xA2";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "0xA2");
  }

  @Test
  public void testNumericLeadingWithZero() {
    String input = "using keyA 012";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "012");
  }

  @Test
  public void testNumericWithSeparator() {
    String input = "using keyA 12_000";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "12_000");
  }

  @Test
  public void testHexNumericWithSeparator() {
    String input = "using keyA 0x1_2";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "0x1_2");
  }

  @Test
  public void testNegativeNumeric() {
    String input = "using keyA -12";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "-12");
  }

  @Test
  public void testHexNegativeNumeric() {
    String input = "using keyA -0xA2";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "-0xA2");
  }

  @Test
  public void testFailTrailingSeparator() {
    PersistentReporter reporter = reporterAfterParsing("using key 200_");
    assertDiagnostic(reporter, 0, 13, "Trailing separator is not allowed");
  }

  @Test
  public void testExponentNumeric() {
    String input = "using keyA 100E+20";
    UsingDirectives parsedDirective = testCode(1, input);
    assertNumericValueAtPath(parsedDirective, "keyA", "100E+20");
  }

  @Test
  public void testFailInvalidHexNumeric() {
    PersistentReporter reporter = reporterAfterParsing("using key 0xZA");
    assertDiagnostic(reporter, 0, 10, "invalid literal number");
  }
}
