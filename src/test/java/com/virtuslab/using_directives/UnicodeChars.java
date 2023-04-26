package com.virtuslab.using_directives;

import static com.virtuslab.using_directives.DirectiveAssertions.assertValueAtPath;
import static com.virtuslab.using_directives.TestUtils.testCode;

import com.virtuslab.using_directives.custom.model.UsingDirectives;
import org.junit.jupiter.api.Test;

public class UnicodeChars {
  @Test
  public void testDifferentQuotes() {
    String input = "//> using nativeMode “release-full”";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "nativeMode", "“release-full”");
  }
}
