package com.virtuslab.using_directives;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.virtuslab.using_directives.custom.model.UsingDirectiveKind;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.reporter.PersistentReporter;

public class TestUtils {

  public static UsingDirectives testCode(
      UsingDirectiveKind expectedKind, int expectedCount, String... examples) {
    String code = joinLines(examples) + "\n// code...";
    UsingDirectivesProcessor processor = new UsingDirectivesProcessor();

    UsingDirectives directives =
        processor
            .extract(
                code.toCharArray(),
                expectedKind == UsingDirectiveKind.SpecialComment,
                expectedKind == UsingDirectiveKind.PlainComment)
            .stream()
            .filter(d -> d.getKind() == expectedKind)
            .findFirst()
            .get();

    assertEquals(expectedCount, directives.getFlattenedMap().size());
    assertEquals(expectedKind, directives.getKind());
    return directives;
  }

  public static UsingDirectives testCode(int expectedCount, String... directives) {
    return testCode(UsingDirectiveKind.Code, expectedCount, directives);
  }

  public static UsingDirectives testCode(String... directives) {
    return testCode(directives.length, directives);
  }

  public static PersistentReporter reporterAfterParsing(String... code) {
    PersistentReporter reporter = new PersistentReporter();
    new UsingDirectivesProcessor(new Context(reporter))
        .extract(joinLines(code).toCharArray(), false, false);
    return reporter;
  }

  public static String joinLines(String... lines) {
    return String.join("\n", lines);
  }
}
