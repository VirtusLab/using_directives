import com.google.gson.Gson;
import dotty.using_directives.custom.Parser;
import dotty.using_directives.custom.UsingDirectivesProcessor;
import dotty.using_directives.custom.Visitor;
import dotty.using_directives.custom.utils.Source;
import dotty.using_directives.custom.utils.ast.*;
import json.CustomGsonInstance;

import java.io.IOException;
import java.net.URISyntaxException;

public class Test extends TestUtils {
    @org.junit.jupiter.api.Test
    public void test() throws IOException, URISyntaxException {
        Gson gson = CustomGsonInstance.get();
        UsingTree ast = new Parser(new Source(getContent("parser_tests/inputs/testcase6.txt").toCharArray())).parse();
        System.out.println(ast);
        int codeOffset = ((UsingDefs) ast).getCodeOffset();
        System.out.println(getContent("parser_tests/inputs/testcase6.txt").substring(codeOffset));
        System.out.println(new Visitor(ast).visit().getFlattenedMap());
        System.out.println(new UsingDirectivesProcessor().extract(getContent("parser_tests/inputs/testcase6.txt").toCharArray()).getFlattenedMap());
    }

}
