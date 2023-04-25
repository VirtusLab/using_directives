package com.virtuslab.using_directives.reporter;

import com.virtuslab.using_directives.DirectiveAssertions;
import org.junit.jupiter.api.Test;

public class PositionTests {

  @Test
  public void testEmptyValuePosition() {
    String allWithValues = "//> using flag true\n//> using flag2 false";
    DirectiveAssertions.assertPositions(allWithValues, new int[] {0, 15}, new int[] {1, 16});

    String firstWithoutValue = "//> using flag\n//> using flag2 false";
    DirectiveAssertions.assertPositions(firstWithoutValue, new int[] {0, 14}, new int[] {1, 16});

    String secondWithoutValue = "//> using flag true\n//> using flag2";
    DirectiveAssertions.assertPositions(secondWithoutValue, new int[] {0, 15}, new int[] {1, 15});

    String allWithoutValue = "//> using flag\n//> using flag2";
    DirectiveAssertions.assertPositions(allWithoutValue, new int[] {0, 14}, new int[] {1, 15});
  }
}
