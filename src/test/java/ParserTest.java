import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import com.virtuslab.using_directives.reporter.ConsoleReporter;
import com.virtuslab.using_directives.reporter.Reporter;
import json.CustomGsonInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ParserTest extends TestUtils {
  private final String inputsRoot = "parser_tests/inputs/";
  private final String resultsRoot = "parser_tests/asts/";

  private UsingTree parseFile(String path) {
    char[] content = getContent(path).toCharArray();
    Reporter reporter = new ConsoleReporter();
    return new Parser(new Source(content), reporter).parse();
  }

  private void compareAST(String pathToInput, String pathToExpectedResult, String pathToConfig) {
    JsonElement expectedAST =
        JsonParser.parseString(getContent(resultsRoot + pathToExpectedResult));
    JsonElement AST = CustomGsonInstance.get().toJsonTree(parseFile(inputsRoot + pathToInput));

    assertEquals(
        expectedAST,
        AST,
        String.format(
            "Test failed for filename: %s\nExpected:\n%sFound:\n%s",
            pathToInput, expectedAST.toString(), AST.toString()));
  }

  // Ignored case 2, 3, 6, 15, 16, 18 since they use removed multiline syntax
  @ParameterizedTest(name = "Run parser testcase no. {0}")
  @ValueSource(ints = {1, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 17, 19, 20, 21, 22})
  public void testParser(int no) {
    compareAST("testcase" + no + ".txt", "ast" + no + ".json", "config" + no + ".json");
  }
}
