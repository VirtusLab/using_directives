import static org.junit.jupiter.api.Assertions.*;

import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.UsingDirectivesProcessor;
import com.virtuslab.using_directives.config.Settings;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import org.junit.jupiter.api.Test;

public class CommentExtractorIntegrationTest extends TestUtils {
  private final String inputsRoot = "comment_extractor_tests/inputs/";
  private final String outputsRoot = "comment_extractor_tests/outputs/";

  private char[] processFile(String path) {
    char[] content = getContent(path).toCharArray();
    return content;
  }

  private UsingDirectives extractDirectives(char[] content) {
    UsingDirectivesProcessor processor = new UsingDirectivesProcessor();
    Context ctx = new Context();
    Settings setts = new Settings();
    setts.setAllowStartWithoutAt(true);
    ctx.setSettings(setts);
    processor.setContext(ctx);
    return processor.extract(content);
  }

  private void integrationTest(String inputPath, String restPath) {
    char[] content = processFile(inputsRoot + inputPath);
    UsingDirectives ud = extractDirectives(content);
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
    integrationTest("comment4.txt", "rest4.txt");
    integrationTest("comment5.txt", "rest5.txt");
  }
}
