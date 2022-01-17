import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.custom.Visitor;
import com.virtuslab.using_directives.custom.model.Path;
import com.virtuslab.using_directives.custom.model.UsingDirectiveKind;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.model.Value;
import com.virtuslab.using_directives.custom.utils.ast.UsingDefs;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import json.CustomGsonInstance;
import org.junit.jupiter.api.Test;

public class VisitorTest extends TestUtils {
  private final String astsRoot = "parser_tests/asts/";
  private final String outputsRoot = "parser_tests/outputs/";

  private UsingTree getAst(String pathToInput) {
    Gson gson = CustomGsonInstance.get();
    return gson.fromJson(getContent(astsRoot + pathToInput), UsingDefs.class);
  }

  private Map<Path, List<Value<?>>> getOutput(String pathToOutput) {
    Gson gson = CustomGsonInstance.get();
    Type type = new TypeToken<Map<Path, ArrayList<Value<?>>>>() {}.getType();
    return gson.fromJson(getContent(outputsRoot + pathToOutput), type);
  }

  private UsingDirectives visitAst(UsingTree ast) {
    Visitor visitor = new Visitor(ast, new Context());
    return visitor.visit(UsingDirectiveKind.Code);
  }

  private void compareOutputs(String pathToAst, String pathToOutput) {
    UsingTree ast = getAst(pathToAst);
    UsingDirectives ud = visitAst(ast);
    Map<Path, List<Value<?>>> expectedFlatView = getOutput(pathToOutput);
    assertEquals(
        ud.getFlattenedMap(),
        expectedFlatView,
        String.format(
            "Test failed for filename: %s\nExpected:\n%s\nFound:\n%s\nJSON:\n%s\n",
            pathToAst,
            expectedFlatView.toString(),
            ud.getFlattenedMap().toString(),
            CustomGsonInstance.get().toJson(ud.getFlattenedMap())));
  }

  @Test
  public void testVisitor() {
    compareOutputs("ast1.json", "output1.txt");
    compareOutputs("ast3.json", "output3.txt");
    compareOutputs("ast4.json", "output4.txt");
    compareOutputs("ast15.json", "output5.txt");
    compareOutputs("ast16.json", "output6.txt");
  }
}
