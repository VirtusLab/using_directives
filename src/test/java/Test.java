import com.google.gson.Gson;
import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.UsingDirectivesProcessor;
import com.virtuslab.using_directives.custom.Visitor;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingDefs;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
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
