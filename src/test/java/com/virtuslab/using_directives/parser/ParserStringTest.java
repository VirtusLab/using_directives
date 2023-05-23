package com.virtuslab.using_directives.parser;

import static com.virtuslab.using_directives.DirectiveAssertions.assertDiagnostic;
import static com.virtuslab.using_directives.DirectiveAssertions.assertValueAtPath;
import static com.virtuslab.using_directives.DirectiveAssertions.assertValueListAtPath;
import static com.virtuslab.using_directives.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import com.virtuslab.using_directives.custom.model.Path;
import com.virtuslab.using_directives.custom.model.StringValue;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.model.Value;
import com.virtuslab.using_directives.custom.utils.ast.StringLiteral;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import com.virtuslab.using_directives.reporter.PersistentReporter;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ParserStringTest {

  @Test
  public void testEscapeInString() {
    String input = "//> using keyA \"ab\\\"\\'\\\\\"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "ab\"'\\");
  }

  @Test
  public void testStringNoQuotes() {
    String input = "//> using keyA ab";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "ab");
  }

  @Test
  public void testStringNoQuotesMultiple() {
    String input = "//> using keyA ab, --ba, -opt, \"asd \"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueListAtPath(parsedDirective, "keyA", List.of("ab", "--ba", "-opt", "asd "));
  }

  @Test
  public void testStringNoQuotesDot() {
    String input = "//> using keyA ab.ba, \"asd \"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueListAtPath(parsedDirective, "keyA", List.of("ab.ba", "asd "));
  }

  @Test
  public void testStringNoQuotesDotNoComma() {
    String input = "//> using keyA ab.ba \"asd \"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueListAtPath(parsedDirective, "keyA", List.of("ab.ba", "asd "));
  }

  @Test
  public void testFailMultilineString() {
    String input = joinLines("//> using keyA \"\"\"line1", "//> line2", "//> line3\"\"\"");
    PersistentReporter reporter = reporterAfterParsing(input);
    assertDiagnostic(reporter, 0, 17, "unclosed string literal");
  }

  @Test
  public void testAllowStringInterpolator() {
    String input = "//> using keyA s\"foo\"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "s\"foo\"");
  }

  @Test
  public void testStringUtfEscape() {
    String input = "//> using foo \"\\u0042\"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "foo", "B");
  }

  @Test
  public void testDoubleQuotesString() {
    String input = "//> using foo \"bar\"";
    UsingDirectives parsedDirective = testCode(1, input);
    Value<?> directiveValue = parsedDirective.getFlattenedMap().get(Path.fromString("foo")).get(0);
    assertInstanceOf(StringValue.class, directiveValue);
    UsingTree astNode = directiveValue.getRelatedASTNode();
    assertInstanceOf(StringLiteral.class, astNode);
    assertTrue(((StringLiteral) astNode).getIsWrappedDoubleQuotes());
    assertValueAtPath(parsedDirective, "foo", "bar");
  }

  @Test
  public void testFailInvalidUtdEscape() {
    PersistentReporter reporter = reporterAfterParsing("//> using foo \"\\u004X\"");
    assertDiagnostic(reporter, 0, 20, "invalid character in unicode escape sequence");
  }

  @Test
  public void testFailOctalEscape() {
    PersistentReporter reporter = reporterAfterParsing("//> using foo \"\\121\"");
    assertDiagnostic(reporter, 0, 14, "octal escape literals are unsupported", "\\u0051");
  }
}
