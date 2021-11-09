import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.virtuslab.using_directives.custom.CommentExtractor;
import com.virtuslab.using_directives.custom.utils.CommentSource;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CommentExtractorTest extends TestUtils {
  private final String inputsRoot = "comment_extractor_tests/inputs/";
  private final String outputsRoot = "comment_extractor_tests/outputs/";

  private CommentSource processFile(String path) {
    char[] content = getContent(path).toCharArray();
    return new CommentExtractor(content).getCommentSource();
  }

  private List<Integer> getExpectedLines(String path) {
    String json = getContent(path);
    JsonArray arr = JsonParser.parseString(json).getAsJsonObject().get("lines").getAsJsonArray();
    return new Gson().fromJson(arr, new TypeToken<ArrayList<Integer>>() {}.getType());
  }

  private void compare(String input, String output, String lines) {
    CommentSource cs = processFile(inputsRoot + input);
    char[] out = getContent(outputsRoot + output).toCharArray();
    List<Integer> expectedLines = getExpectedLines(outputsRoot + lines);
    assertArrayEquals(
        out,
        cs.getContent(),
        String.format(
            "Test failed for filename: %s\nExpected:\n%sFound:\n%s",
            input, new String(out), new String(cs.getContent())));
    assertEquals(
        cs.lineOffsets,
        expectedLines,
        String.format(
            "Lines offset don't match for filename: %s\nExpected:\n%sFound:\n%s",
            input, expectedLines, cs.lineOffsets));
  }

  @Test
  public void testCommentExtractor() {
    compare("comment1.txt", "output1.txt", "lines1.json");
    compare("comment2.txt", "output2.txt", "lines2.json");
    compare("comment3.txt", "output3.txt", "lines3.json");
  }
}
