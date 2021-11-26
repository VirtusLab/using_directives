import com.google.gson.Gson;
import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.UsingDirectivesProcessor;
import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.Visitor;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingDefs;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import java.io.IOException;
import java.net.URISyntaxException;
import json.CustomGsonInstance;

public class Test extends TestUtils {

  @org.junit.jupiter.api.Test
  public void test() throws IOException, URISyntaxException {
    Gson gson = CustomGsonInstance.get();
    Context ctx = new Context();
    UsingTree ast =
        new Parser(new Source(getContent("parser_tests/inputs/testcase14.txt").toCharArray()), ctx)
            .parse();
    System.out.println(ast);
    System.out.println(gson.toJson(ast));
    int codeOffset = ((UsingDefs) ast).getCodeOffset();
    System.out.println(
        getContent("comment_extractor_tests/inputs/comment1.txt").substring(codeOffset));
    System.out.println(new Visitor(ast, ctx).visit().getFlattenedMap());
    UsingDirectives ud =
        new UsingDirectivesProcessor()
            .extract(getContent("comment_extractor_tests/inputs/comment2.txt").toCharArray());
    System.out.println(ud.getFlattenedMap());
    System.out.println(ud.toString());
    codeOffset = ud.getCodeOffset();
    System.out.println(ud.getCodeOffset());
    System.out.println(
        getContent("comment_extractor_tests/inputs/comment2.txt").substring(codeOffset));
  }
}
