import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

class TestUtils {
  public String getContent(String path) {
    InputStream is = getClass().getResourceAsStream(path);
    try {
      byte[] bytes = IOUtils.toByteArray(is);
      return new String(bytes);
    } catch (IOException io) {
      return null;
    }
  }
}
