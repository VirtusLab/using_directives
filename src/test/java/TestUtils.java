import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

class TestUtils {
    public String getContent(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return br.lines().collect(Collectors.joining("\n"));
    }
}
