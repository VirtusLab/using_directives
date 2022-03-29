package com.virtuslab.using_directives;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.virtuslab.using_directives.custom.model.UsingDirectiveKind;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.reporter.PersistentReporter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TestUtils {

  public static UsingDirectives testCode(
      UsingDirectiveKind expectedKind, int expectedCount, String... examples) {
    String code = Arrays.stream(examples).collect(Collectors.joining("\n")) + "\n// code...";
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

  public static UsingDirectives testSpecialComments(String... directives) {
    return testCode(UsingDirectiveKind.SpecialComment, directives.length, directives);
  }

  public static UsingDirectives testCode(String... directives) {
    return testCode(UsingDirectiveKind.Code, directives.length, directives);
  }

  public static UsingDirectives testDirectives(String... directives) {
    return testCode(UsingDirectiveKind.Code, directives.length, directives);
  }

  public static PersistentReporter reporterAfterParsing(String code) {
    PersistentReporter reporter = new PersistentReporter();
    new UsingDirectivesProcessor(new Context(reporter)).extract(code.toCharArray(), false, false);

    return reporter;
  }
}
