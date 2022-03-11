package com.virtuslab.using_directives.reporter;

import static org.junit.jupiter.api.Assertions.*;

import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.UsingDirectivesProcessor;
import org.junit.jupiter.api.Test;

class ReporterTest {

  private PersistentReporter runTest(String code) {
    PersistentReporter reporter = new PersistentReporter();
    new UsingDirectivesProcessor(new Context(reporter)).extract(code.toCharArray(), false, false);

    return reporter;
  }

  private void checkDiag(
      PersistentReporter.Diagnostic diag,
      int expectedLine,
      int expectedColumn,
      String... expectedWords) {
    for (String word : expectedWords)
      assertTrue(
          diag.getMessage().contains(word),
          "Message should mention '" + word + "' but got : " + diag.getMessage());
    assertEquals(expectedLine, diag.getPosition().get().getLine());
    assertEquals(expectedColumn, diag.getPosition().get().getColumn());
  }

  @Test
  public void reportNotQuotedString() {
    PersistentReporter reporter = runTest("\n\n  using options -Xfatal-warnings");
    reporter.getDiagnostics().forEach(d -> System.out.println(d.getMessage()));
    assertTrue(reporter.hasErrors());
    assertEquals(1, reporter.getDiagnostics().size());


    checkDiag(
        reporter.getDiagnostics().get(0),
        2,
        17,
        "string",
        "numeric",
        "boolean",
        "identifier",
        "quotes");
  }
}
