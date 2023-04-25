package com.virtuslab.using_directives;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.reporter.PersistentReporter;

public class TestUtils {

  public static UsingDirectives testCode(int expectedCount, String... examples) {
    String code = joinLines(examples) + "\n// code...";
    UsingDirectivesProcessor processor = new UsingDirectivesProcessor();

    List<UsingDirectives> directives =  processor.extract(code.toCharArray());

    assertEquals(1, directives.size(), "We should only get a single directive object");
    assertEquals(expectedCount, directives.get(0).getFlattenedMap().size());
    return directives.get(0);
  }

  public static UsingDirectives testCode(String... directives) {
    return testCode(directives.length, directives);
  }

  public static PersistentReporter reporterAfterParsing(String... code) {
    PersistentReporter reporter = new PersistentReporter();
    new UsingDirectivesProcessor(reporter).extract(joinLines(code).toCharArray());
    return reporter;
  }

  public static String joinLines(String... lines) {
    return String.join("\n", lines);
  }
}
