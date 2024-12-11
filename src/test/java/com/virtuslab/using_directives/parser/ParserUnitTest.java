package com.virtuslab.using_directives.parser;

import static com.virtuslab.using_directives.DirectiveAssertions.*;
import static com.virtuslab.using_directives.TestUtils.*;

import com.virtuslab.using_directives.custom.model.Path;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.model.Value;
import com.virtuslab.using_directives.reporter.PersistentReporter;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ParserUnitTest {

  @Test
  public void testNestedAndValuesOnOneLevel() {
    String input =
        joinLines(
            "//> using", "//>   keyA \"valueA\"", "//> using   keyB.keyC  ", "//>      \"valueC\"");
    UsingDirectives parsedDirective = testCode(2, input);
    assertSamePaths(parsedDirective, "keyA", "keyB.keyC");
  }

  @Test
  public void testEmptyListElem() {
    String input = "//> using keyA 0, , 2";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueListSize(parsedDirective, "keyA", 3);
  }

  @Test
  public void testListNoComma() {
    String input = "//> using keyA 0 2 3";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueListSize(parsedDirective, "keyA", 3);
    assertValueListAtPath(parsedDirective, "keyA", List.of("0", "2", "3"));
  }

  @Test
  public void testListComma() {
    String input = "//> using keyA 0, 2, 3";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueListSize(parsedDirective, "keyA", 3);
    assertValueListAtPath(parsedDirective, "keyA", List.of("0", "2", "3"));
  }

  @Test
  public void testBooleanLiteral() {
    String input = "//> using keyA false";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "false");
  }

  @Test
  public void testTokenOnTemplateStart() {
    String input = joinLines("//> using keyA:", "//> using keyB 42");
    UsingDirectives parsedDirective = testCode(2, input);
    assertValueAtPath(parsedDirective, "keyA:", "<EmptyValue>");
    assertValueAtPath(parsedDirective, "keyB", "42");
  }

  @Test
  public void testParenKey() {
    String input = "//> using keyA.( \"foo\"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA.(", "foo");
  }

  @Test
  public void testInfixOperator() {
    String input = "//> using keyA -2 ";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "-2");
  }

  @Test
  public void testIndentation() {
    String input = joinLines("//> using keyA:", "   keyB", "  keyC");
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA:", "<EmptyValue>");
  }

  @Test
  public void testMixedIndenttion() {
    String input = joinLines("//> using keyA:\t\t keyB", " \t\tkeyC");
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA:", "keyB");
  }

  @Test
  public void testUnderscoreIdentifier() {
    String input = "//> using keyA_foo_ 42";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA_foo_", "42");
  }

  @Test
  public void testBackquotedIdentifier() {
    String input = "//> using `keyA` 42";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "42");
  }

  @Test
  public void testUnicodeIdentifier() {
    String input = "//> using keyĄ 42";
    UsingDirectives parsedDirective = testCode(1, input);
    PersistentReporter reporter = reporterAfterParsing(input);
    reporter.getDiagnostics().forEach(c -> System.out.println(c.getMessage()));
    assertValueAtPath(parsedDirective, "keyĄ", "42");
  }

  @Test
  public void testSlashIdentifier() {
    String input = "//> using >/> 42";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, ">/>", "42");
  }

  @Test
  public void emptySingleDotKey() {
    String input = "//> using lib.allowSth\n";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "lib.allowSth", "<EmptyValue>");
  }

  @Test
  public void testMathSymbolIdentifier() {
    String input = "//> using >/∑ 42";
    System.out.println(input);
    UsingDirectives parsedDirective = testCode(1, input);
    PersistentReporter reporter = reporterAfterParsing(input);
    reporter.getDiagnostics().forEach(c -> System.out.println(c.getMessage()));
    assertValueAtPath(parsedDirective, ">/∑", "42");
  }

  @Test
  public void testFailUnclosedQuotedIdentifier() {
    PersistentReporter reporter = reporterAfterParsing("using `keyA 42");
    assertDiagnostic(reporter, 0, 6, "unclosed quoted identifier");
  }

  @Test
  public void deprecatedCommas() {
    PersistentReporter reporter = reporterAfterParsing("using keyA 42, 34, 55");
    assertDiagnostic(
        reporter,
        0,
        13,
        "Use of commas as separators is deprecated. Only whitespace is neccessary.");
  }

  @Test
  public void testFailWildcardQuotedIdentifier() {
    PersistentReporter reporter = reporterAfterParsing("using `keyA_foo` 42");
    assertDiagnostic(reporter, 0, 6, "wildcard invalid as backquoted identifier");
  }

  @Test
  public void testFailEmptyQuotedIdentifier() {
    PersistentReporter reporter = reporterAfterParsing("using `` 42");
    assertDiagnostic(reporter, 0, 6, "empty quoted identifier");
  }

  @Test
  public void testSkipMultilineComment() {
    String input = joinLines("using keyA:", "   keyB 42", "\\* ", "using keyC 2137", "*\\");
    PersistentReporter reporter = reporterAfterParsing(input);
    assertDiagnostic(
        reporter, 1, 8, "Expected new line after the using directive, in the line; but found");
  }

  @Test
  public void testInvalidPrimitive() {
    String input = "using dep com.lihaoyi :: fastparse : 3.0.2";
    PersistentReporter reporter = reporterAfterParsing(input);
    assertDiagnostic(reporter, 0, 35, "Invalid primitive: :");
  }

  @Test
  public void testListJustComma() {
    String input = "//> using keyA 0,2,3";
    UsingDirectives parsedDirective = testCode(1, input);
    List<Value<?>> values = parsedDirective.getFlattenedMap().get(Path.fromString("keyA"));
    System.out.println(parsedDirective);
    System.out.println("values " + values.size());
    assertValueListSize(parsedDirective, "keyA", 1);
    assertValueListAtPath(parsedDirective, "keyA", List.of("0,2,3"));
  }
}
