package syntax;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import syntax.analyzer.SA;
import syntax.generator.GSA;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

    @ParameterizedTest
    @MethodSource("provideTestDirectoryNames")
    public void integrationTest(String directoryName) throws IOException, ClassNotFoundException {
        String pathPrefix = "./src/test/resources/test-examples/";

        String sanFileName = pathPrefix + directoryName + "/test.san";
        String inFileName = pathPrefix + directoryName + "/test.in";
        String outFileName = pathPrefix + directoryName + "/test.out";
        String myFileName = pathPrefix + directoryName + "/test.my";

        //run generator
        GSA gsa = new GSA();
        try(InputStream in = new FileInputStream(sanFileName)){
            gsa.parseInput(in);
            gsa.writeSAConfigObjects();
        }

        //run analyzer
        try(InputStream input = new FileInputStream(inFileName);
            PrintStream output = new PrintStream(new FileOutputStream(myFileName))){

            SA sa = new SA();
            sa.readSAConfigObject();

            sa.analyzeInput(input, output);
        }

        String myOutput = Files.readString(Path.of(myFileName));
        String correctOutput = Files.readString(Path.of(outFileName));

        assertEquals(normalizeString(correctOutput), normalizeString(myOutput));
    }

    private static Stream<Arguments> provideTestDirectoryNames() {

        File[] directories = new File("./src/test/resources/test-examples").listFiles(File::isDirectory);

        return Arrays.stream(directories).map(File::getName).map(Arguments::of);
    }

    public static String normalizeString(String s){
        return s.replace("\r\n", "\n")
                .replace("\r", "\n").trim();
    }
}
