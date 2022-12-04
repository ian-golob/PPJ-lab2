package syntax;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import syntax.analyzer.SA;
import syntax.generator.GSA;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

    @ParameterizedTest
    @MethodSource("provideNewTestDirectoryNames")
    public void integrationTest(String directoryName) throws IOException, ClassNotFoundException {
        String pathPrefix = "./src/test/resources/" + directoryName;

        String sanFileName = pathPrefix + "/test.san";
        String inFileName = pathPrefix + "/test.in";
        String outFileName = pathPrefix + "/test.out";
        String myFileName = pathPrefix + "/test.my";
        String objFileName = pathPrefix + "/config.obj";

        //run generator
        GSA gsa = new GSA();
        try(InputStream in = new FileInputStream(sanFileName)){
            gsa.parseInput(in);
            gsa.writeSAConfigObjects(objFileName);
        }

        //run analyzer
        try(InputStream input = new FileInputStream(inFileName);
            PrintStream output = new PrintStream(new FileOutputStream(myFileName))){

            SA sa = new SA();
            sa.readSAConfigObject(objFileName);

            sa.analyzeInput(input, output);
        }

        String myOutput = Files.readString(Path.of(myFileName));
        String correctOutput = Files.readString(Path.of(outFileName));

        assertEquals(normalizeString(correctOutput), normalizeString(myOutput));
    }


    private static Stream<Arguments> provideNewTestDirectoryNames() {

        File[] directoriesNew = new File("./src/test/resources/test-examples").listFiles(File::isDirectory);
        File[] directories1112 = new File("./src/test/resources/test-examples-11-12").listFiles(File::isDirectory);

        List<String> args = new ArrayList<>();

        for(File file: directoriesNew){
            args.add("test-examples/"+file.getName());
        }

        for(File file: directories1112){
            args.add("test-examples-11-12/"+file.getName());
        }


        return args.stream().map(Arguments::of);
    }

    public static String normalizeString(String s){
        return s.replace("\r\n", "\n")
                .replace("\r", "\n").trim();
    }
}
