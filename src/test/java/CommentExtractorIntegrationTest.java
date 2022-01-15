import static org.junit.jupiter.api.Assertions.*;

import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.UsingDirectivesProcessor;
import com.virtuslab.using_directives.config.Settings;
import com.virtuslab.using_directives.custom.model.UsingDirectiveKind;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    UsingDirectives res = processor.extract(content, true, false).get(1);
    assertFalse(processor.getContext().getReporter().hasErrors());
    assertEquals(UsingDirectiveKind.SpecialComment, res.getKind());
    return res;
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
            "Test failed for filename: %s\nExpected:\n%s\nFound:\n%s",
            inputPath, expectedRest, foundRest));
  }

  // TODO make 8 work
  @ParameterizedTest(name = "Run comment extractor testcase no. {0}")
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
  public void tests(int no) {
    integrationTest("comment" + no + ".txt", "rest" + no + ".txt");
  }
}
