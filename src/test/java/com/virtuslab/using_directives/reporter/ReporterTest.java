package com.virtuslab.using_directives.reporter;

import static com.virtuslab.using_directives.DirectiveAssertions.assertDiagnostic;
import static com.virtuslab.using_directives.TestUtils.reporterAfterParsing;
import static org.junit.jupiter.api.Assertions.*;

import com.virtuslab.using_directives.DirectiveAssertions.*;
import org.junit.jupiter.api.Test;

class ReporterTest {

  @Test
  public void reportNotClosedQuotes() {
    PersistentReporter reporter = reporterAfterParsing("using options \"Xfatal-warnings\n");
    assertTrue(reporter.hasErrors());
    assertEquals(1, reporter.getDiagnostics().size());

    assertDiagnostic(reporter.getDiagnostics().get(0), 0, 14, "unclosed string literal");
  }
}
