package com.virtuslab.using_directives.parser;

import static com.virtuslab.using_directives.DirectiveAssertions.*;
import static com.virtuslab.using_directives.TestUtils.*;

import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.reporter.PersistentReporter;
import org.junit.jupiter.api.Test;

public class ParserScopeTest {

  @Test
  public void testMultipleScopes() {
    String input =
        joinLines("using", "   keyA \"fooA\" in \"scopeA\"", "   keyA \"fooB\" in \"scopeB\"");
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPathAndScope(parsedDirective, "keyA", "scopeA", "fooA");
    assertValueAtPathAndScope(parsedDirective, "keyA", "scopeB", "fooB");
  }

  @Test
  public void testMultipleValuesOneScope() {
    String input = "using keyA \"fooA\", \"fooB\" in \"scopeA\"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueListSizeAtScope(parsedDirective, "keyA", "scopeA", 2);
  }

  @Test
  public void testFailScopeNotAString() {
    String input = "using keyA \"fooA\", \"fooB\" in 2";
    PersistentReporter reporter = reporterAfterParsing(input);
    assertDiagnostic(reporter, 0, 29, "Expected token STRINGLIT but found", "integer literal");
  }

  @Test
  public void testFailScopeMissing() {
    String input = joinLines("using", "   keyA \"fooA\" in ", "   keyA \"fooB\" in \"scopeB\"");
    PersistentReporter reporter = reporterAfterParsing(input);
    assertDiagnostic(reporter, 2, 3, "Expected token STRINGLIT but found");
  }
}
