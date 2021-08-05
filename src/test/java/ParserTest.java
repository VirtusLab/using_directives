import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dotty.using_directives.custom.Parser;
import dotty.using_directives.custom.utils.Source;
import dotty.using_directives.custom.utils.ast.UsingTree;

import static org.junit.jupiter.api.Assertions.*;

import json.CustomGsonInstance;
import org.junit.jupiter.api.Test;

public class ParserTest extends TestUtils {
    private UsingTree parseFile(String path) {
        char[] content = getContent(path).toCharArray();
        return new Parser(new Source(content)).parse();
    }

    private void compareAST(String pathToInput, String pathToExpectedResult) {
        JsonElement expectedAST = JsonParser.parseString(getContent(pathToExpectedResult));
        JsonElement AST = CustomGsonInstance.get().toJsonTree(parseFile(pathToInput));
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
        compareAST("parser_tests/inputs/testcase1.txt", "parser_tests/asts/ast1.txt");
        compareAST("parser_tests/inputs/testcase2.txt", "parser_tests/asts/ast2.txt");
        compareAST("parser_tests/inputs/testcase3.txt", "parser_tests/asts/ast3.txt");
        compareAST("parser_tests/inputs/testcase4.txt", "parser_tests/asts/ast4.txt");
        compareAST("parser_tests/inputs/testcase5.txt", "parser_tests/asts/ast5.txt");
    }
}
