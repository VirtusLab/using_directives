package com.virtuslab.using_directives;

import static com.virtuslab.using_directives.TestUtils.testCode;
import static org.junit.jupiter.api.Assertions.*;

import com.virtuslab.using_directives.custom.model.Path;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.model.Value;
import com.virtuslab.using_directives.reporter.PersistentReporter;
import java.util.*;
import java.util.stream.Collectors;

public class DirectiveAssertions {
  public static void assertSamePaths(UsingDirectives directives, String... expectedPaths) {
    Set<String> paths =
        directives.getFlattenedMap().keySet().stream()
            .map(Path::toString)
            .collect(Collectors.toSet());
    Set<String> expectedPathsSet = new HashSet<>(Arrays.asList(expectedPaths));
    assertEquals(expectedPathsSet, paths);
  }

  public static void assertValueAtPath(
      UsingDirectives directives, String path, String expectedValueString) {
    List<Value<?>> values = directives.getFlattenedMap().get(Path.fromString(path));
    assertNotNull(values);
    assertEquals(1, values.size());
    assertEquals(expectedValueString, values.get(0).toString());
  }

  public static void assertValueListAtPath(
      UsingDirectives directives, String path, List<String> expectedValueString) {
    List<Value<?>> values = directives.getFlattenedMap().get(Path.fromString(path));
    assertNotNull(values);
    assertEquals(expectedValueString.size(), values.size());
    assertIterableEquals(
        expectedValueString, values.stream().map(Value::toString).collect(Collectors.toList()));
  }

  public static void assertNoValueAtPath(UsingDirectives directives, String path) {
    boolean containsKey = directives.getFlattenedMap().containsKey(Path.fromString(path));
    assertFalse(containsKey);
  }

  public static void assertValueListSize(
      UsingDirectives directives, String path, int assertedSize) {
    List<Value<?>> values = directives.getFlattenedMap().get(Path.fromString(path));
    assertNotNull(values);
    assertEquals(assertedSize, values.size());
  }

  public static void assertValueAtPathAndScope(
      UsingDirectives directives, String path, String scope, String expectedValueString) {
    List<Value<?>> values = directives.getFlattenedMap().get(Path.fromString(path));
    assertNotNull(values);
    List<Value<?>> scopeValues =
        values.stream().filter(f -> f.getScope().equals(scope)).collect(Collectors.toList());
    assertEquals(1, scopeValues.size());
    assertEquals(expectedValueString, scopeValues.get(0).toString());
  }

  public static void assertValueListSizeAtScope(
      UsingDirectives directives, String path, String scope, int assertedSize) {
    List<Value<?>> values = directives.getFlattenedMap().get(Path.fromString(path));
    List<Value<?>> scopeValues =
        values.stream().filter(f -> f.getScope().equals(scope)).collect(Collectors.toList());
    assertEquals(assertedSize, scopeValues.size());
  }

  public static void assertDiagnostic(
      PersistentReporter.Diagnostic diag,
      int expectedLine,
      int expectedColumn,
      String... expectedWords) {
    for (String word : expectedWords)
      assertTrue(
          diag.getMessage().contains(word),
          "Message should mention '" + word + "' but got : " + diag.getMessage());
    assertTrue(diag.getPosition().isPresent());
    assertEquals(expectedLine, diag.getPosition().get().getLine());
    assertEquals(expectedColumn, diag.getPosition().get().getColumn());
  }

  public static void assertDiagnostic(
      PersistentReporter reporter, int expectedLine, int expectedColumn, String... expectedWords) {
    assertTrue(reporter.hasErrors());
    assertDiagnostic(reporter.getDiagnostics().get(0), expectedLine, expectedColumn, expectedWords);
  }

  public static void assertPosition(UsingDirectives ud, int line, int column) {
    List<Value<?>> byLine =
        ud.getFlattenedMap().values().stream()
            .flatMap(v -> v.stream())
            .filter(v -> v.getRelatedASTNode().getPosition().getLine() == line)
            .collect(Collectors.toList());
    assertEquals(byLine.size(), 1, "More then one value in line " + line);
    assertEquals(
        column,
        byLine.get(0).getRelatedASTNode().getPosition().getColumn(),
        "Wrong index for line " + line);
  }

  public static void assertPositions(String code, int[]... expectedPositions) {
    UsingDirectives ud = testCode(expectedPositions.length, code);
    Arrays.stream(expectedPositions).forEach(pos -> assertPosition(ud, pos[0], pos[1]));
  }
}
