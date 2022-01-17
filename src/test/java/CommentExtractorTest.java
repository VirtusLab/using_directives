import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.virtuslab.using_directives.custom.SimpleCommentExtractor;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CommentExtractorTest extends TestUtils {
  private final String inputsRoot = "comment_extractor_tests/inputs/";
  private final String outputsRoot = "comment_extractor_tests/outputs/";

  private char[] processFile(String path) {
    char[] content = getContent(path).toCharArray();
    return new SimpleCommentExtractor(content, true).extractComments();
  }

  private List<Integer> getExpectedLines(String path) {
    String json = getContent(path);
    JsonArray arr = JsonParser.parseString(json).getAsJsonObject().get("lines").getAsJsonArray();
    return new Gson().fromJson(arr, new TypeToken<ArrayList<Integer>>() {}.getType());
  }

  private void compare(String input, String output) {
    String processed = new String(processFile(inputsRoot + input)).trim();
    String expected = getContent(outputsRoot + output).trim();
    assertEquals(expected, processed, "Test failed for: " + input);
  }

  @ParameterizedTest(name = "Run comment extractor testcase no. {0}")
  @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
  public void testCommentExtractor(int no) {
    compare("comment" + no + ".txt", "output" + no + ".txt");
  }
}
