package syntax.generator;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class GSATest {

    @Test
    public void testParsing() throws IOException {
        GSA gsa = new GSA();


        try(FileInputStream inputStream = new FileInputStream("./src/test/resources/example.txt")){
            gsa.parseInput(inputStream);
        }

    }

}
