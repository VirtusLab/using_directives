import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.config.Settings;
import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import json.CustomGsonInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ParserTest extends TestUtils {
  private final String inputsRoot = "parser_tests/inputs/";
  private final String resultsRoot = "parser_tests/asts/";
  private final String configsRoot = "parser_tests/configs/";

  private UsingTree parseFile(String path, Settings settings) {
    char[] content = getContent(path).toCharArray();
    Context ctx = new Context(settings);
    return new Parser(new Source(content), ctx).parse();
  }

  private void compareAST(String pathToInput, String pathToExpectedResult, String pathToConfig) {
    JsonElement expectedAST =
        JsonParser.parseString(getContent(resultsRoot + pathToExpectedResult));
    Settings settings = new Gson().fromJson(getContent(configsRoot + pathToConfig), Settings.class);
    JsonElement AST =
        CustomGsonInstance.get().toJsonTree(parseFile(inputsRoot + pathToInput, settings));

    assertEquals(
        expectedAST,
        AST,
        String.format(
            "Test failed for filename: %s\nExpected:\n%sFound:\n%s",
            pathToInput, expectedAST.toString(), AST.toString()));
  }

  @ParameterizedTest(name = "Run parser testcase no. {0}")
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21})
  public void testParser(int no) {
    compareAST("testcase" + no + ".txt", "ast" + no + ".json", "config" + no + ".json");
  }
}
