import com.google.gson.Gson;
import com.virtuslab.using_directives.Context;
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
        Context ctx = new Context();
        UsingTree ast = new Parser(new Source(getContent("parser_tests/inputs/testcase9.txt").toCharArray()), ctx).parse();
        System.out.println(ast);
        int codeOffset = ((UsingDefs) ast).getCodeOffset();
        System.out.println(getContent("parser_tests/inputs/testcase9.txt").substring(codeOffset));
        System.out.println(new Visitor(ast, ctx).visit().getFlattenedMap());
        System.out.println(new UsingDirectivesProcessor().extract(getContent("parser_tests/inputs/testcase9.txt").toCharArray()).getFlattenedMap());
    }

}
