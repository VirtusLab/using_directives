package com.virtuslab.using_directives.reporter;

import static com.virtuslab.using_directives.DirectiveAssertions.assertDiagnostic;
import static com.virtuslab.using_directives.TestUtils.reporterAfterParsing;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReporterTest {

  @Test
  public void reportNotQuotedString() {
    PersistentReporter reporter = reporterAfterParsing("\n\n  using options Xfatal-warnings");
    assertTrue(reporter.hasErrors());
    /* There are two errors reported:
     - one that reports Xfatal-warnings is not a valid value
     - one that reports that there's something left in the line
     Therefore, we need to check for 2 errors
    */
    assertEquals(2, reporter.getDiagnostics().size());

    assertDiagnostic(
        reporter.getDiagnostics().get(0),
        2,
        16,
        "string",
        "numeric",
        "boolean",
        "identifier",
        "quotes");
  }
}
