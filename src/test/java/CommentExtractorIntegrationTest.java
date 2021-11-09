import static org.junit.jupiter.api.Assertions.*;

import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.custom.CommentExtractor;
import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.utils.CommentSource;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingDefs;
import org.junit.jupiter.api.Test;

public class CommentExtractorIntegrationTest extends TestUtils {
  private final String inputsRoot = "comment_extractor_tests/inputs/";
  private final String outputsRoot = "comment_extractor_tests/outputs/";

  private CommentSource processFile(String path) {
    char[] content = getContent(path).toCharArray();
    return new CommentExtractor(content).getCommentSource();
  }

  private UsingDefs parseSource(Source source) {
    return new Parser(source, new Context()).parse();
  }

  private void integrationTest(String inputPath, String restPath) {
    Source source = processFile(inputsRoot + inputPath);
    UsingDefs ud = parseSource(source);
    int codeOffset = ud.getCodeOffset();
    String foundRest = getContent(inputsRoot + inputPath).substring(codeOffset);
    String expectedRest = getContent(outputsRoot + restPath);

    assertEquals(
        expectedRest,
        foundRest,
        String.format(
            "Test failed for filename: %s\nExpected:\n%sFound:\n%s",
            inputPath, expectedRest, foundRest));
  }

  @Test
  public void tests() {
    integrationTest("comment1.txt", "rest1.txt");
    integrationTest("comment2.txt", "rest2.txt");
    integrationTest("comment3.txt", "rest3.txt");
  }
}
