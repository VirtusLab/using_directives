package com.virtuslab.using_directives;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UnicodeChars {
  @Test
  public void testDifferentQuotes() {
    String code = "using nativeMode “release-full”";

    var reporter = TestUtils.reporterAfterParsing(code);
    assertTrue(reporter.hasErrors());
    assertTrue(reporter.getDiagnostics().stream().count() > 0);
  }

  @Test
  public void failOnInterpolatedStrings() {
    String code = "using lib ivy\"org.scala-sbt::io:1.6.0\"";
    var reporter = TestUtils.reporterAfterParsing(code);
    assertTrue(reporter.hasErrors());
    assertTrue(
        reporter.getDiagnostics().stream()
                .filter(d -> d.getMessage().contains("interpolator"))
                .count()
            > 0);
  }
}
