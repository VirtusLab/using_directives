
import dotty.using_directives.custom.Parser;
import dotty.using_directives.custom.Scanner;
import dotty.using_directives.custom.Tokens;
import dotty.using_directives.custom.utils.ast.UsingTree;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
    @org.junit.jupiter.api.Test
    public void test() throws IOException, URISyntaxException {
        String tc1 = "testcase2.txt";
        char[] content = new String(Files.readAllBytes(Paths.get(getClass().getResource(tc1).toURI()))).toCharArray();
        
        // Scanner scanner = new Scanner(content, 0);
        // while(scanner.td.token != Tokens.EOF) {
        //     System.out.println(scanner.show());
        //     scanner.nextToken();
        // }

        Parser parser = new Parser(content);
        UsingTree tree = parser.parse();
        System.out.println(tree.toString());
    }

}
