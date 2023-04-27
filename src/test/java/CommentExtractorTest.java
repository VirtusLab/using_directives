import static org.junit.jupiter.api.Assertions.*;

import com.virtuslab.using_directives.custom.SimpleCommentExtractor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CommentExtractorTest extends TestUtils {
  private final String inputsRoot = "comment_extractor_tests/inputs/";
  private final String outputsRoot = "comment_extractor_tests/outputs/";

  private char[] processFile(String path) {
    char[] content = getContent(path).toCharArray();
    return new SimpleCommentExtractor(content, true).extractComments();
  }

  private void compare(String input, String output) {
    String processed = new String(processFile(inputsRoot + input)).trim();
    String expected = getContent(outputsRoot + output).trim();
    assertEquals(expected, processed, "Test failed for: " + input);
  }

  @ParameterizedTest(name = "Run comment extractor testcase no. {0}")
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
  public void testCommentExtractor(int no) {
    compare("comment" + no + ".txt", "output" + no + ".txt");
  }
}
