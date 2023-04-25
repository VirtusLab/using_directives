package com.virtuslab.using_directives.parser;

import static com.virtuslab.using_directives.DirectiveAssertions.assertDiagnostic;
import static com.virtuslab.using_directives.DirectiveAssertions.assertValueAtPath;
import static com.virtuslab.using_directives.DirectiveAssertions.assertValueListAtPath;
import static com.virtuslab.using_directives.TestUtils.*;

import com.virtuslab.using_directives.custom.model.UsingDirectives;
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
  public void testMultilineString() {
    String input = joinLines("//> using keyA \"\"\"line1", "//> line2", "//> line3\"\"\"");
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "line1\nline2\nline3");
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
