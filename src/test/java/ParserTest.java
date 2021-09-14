import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.config.Settings;
import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import json.CustomGsonInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest extends TestUtils {
    private final String inputsRoot = "parser_tests/inputs/";
    private final String resultsRoot = "parser_tests/asts/";
    private final String configsRoot = "parser_tests/configs/";

    private UsingTree parseFile(String path, Settings settings) {
        char[] content = getContent(path).toCharArray();
        return new Parser(new Source(content), new Context()).parse();
    }

    private void compareAST(String pathToInput, String pathToExpectedResult, String pathToConfig) {
        JsonElement expectedAST = JsonParser.parseString(getContent(resultsRoot + pathToExpectedResult));
        Settings settings = new Gson().fromJson(getContent(configsRoot + pathToConfig), Settings.class);
        JsonElement AST = CustomGsonInstance.get().toJsonTree(parseFile(inputsRoot + pathToInput, settings));

        assertEquals(
                expectedAST,
                AST,
                String.format(
                        "Test failed for filename: %s\nExpected:\n%sFound:\n%s",
                        pathToInput,
                        expectedAST.toString(),
                        AST.toString()
                )
        );
    }

    @Test
    public void testParser() {
        compareAST("testcase1.txt", "ast1.txt", "config1.json");
        compareAST("testcase2.txt", "ast2.txt", "config2.json");
        compareAST("testcase3.txt", "ast3.txt", "config3.json");
        compareAST("testcase4.txt", "ast4.txt", "config4.json");
        compareAST("testcase5.txt", "ast5.txt", "config5.json");
        compareAST("testcase6.txt", "ast6.txt", "config6.json");
        compareAST("testcase7.txt", "ast7.txt", "config7.json");
        compareAST("testcase8.txt", "ast8.txt", "config8.json");
        compareAST("testcase9.txt", "ast9.txt", "config9.json");
        compareAST("testcase10.txt", "ast10.txt", "config10.json");
        compareAST("testcase11.txt", "ast11.txt", "config11.json");
    }
}
