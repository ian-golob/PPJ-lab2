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

        System.out.println("------------- Productions -------------");
        for(NonTerminalSymbol nonTerminalSymbol: gsa.getNonTerminalSymbols()){
            for(Production p: nonTerminalSymbol.getProductions()){
                System.out.println(p);
            }
        }
        System.out.println("------------- Productions -------------");

        System.out.println();

        System.out.println("------------ Empty symbols ------------");
        for(NonTerminalSymbol symbol: gsa.getNonTerminalSymbols()){
            if(symbol.isEmptySymbol()){
                System.out.print(symbol);
            }
        }
        System.out.println();
        System.out.println("------------ Empty symbols ------------");

        System.out.println();

        System.out.println("------------- Starts sets -------------");
        for(NonTerminalSymbol nonTerminalSymbol: gsa.getNonTerminalSymbols()){
            System.out.println("STARTS(" + nonTerminalSymbol + ") = " + nonTerminalSymbol.starts());
        }
        System.out.println("------------- Starts sets -------------");
        System.out.println();

    }

}
