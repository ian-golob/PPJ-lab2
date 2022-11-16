package syntax.generator;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

public class GSATest {

    @Test
    public void testParsing() throws IOException {
        GSA gsa = new GSA();


        try(FileInputStream inputStream = new FileInputStream("./src/test/resources/simple-example.txt")){
            gsa.parseInput(inputStream);
        }

        System.out.println("------------- Productions -------------");
        for(NonTerminalSymbol nonTerminalSymbol: gsa.getNonTerminalSymbols()){
            for(Production p: nonTerminalSymbol.getProductions()){
                System.out.println(p);
            }
        }
        System.out.println("---------------------------------------");

        System.out.println();

        System.out.println("------------ Empty symbols ------------");
        for(NonTerminalSymbol symbol: gsa.getNonTerminalSymbols()){
            if(symbol.isEmptySymbol()){
                System.out.print(symbol);
            }
        }
        System.out.println();
        System.out.println("---------------------------------------");

        System.out.println();

        System.out.println("------------- STARTS sets -------------");
        for(NonTerminalSymbol nonTerminalSymbol: gsa.getNonTerminalSymbols()){
            System.out.println("STARTS(" + nonTerminalSymbol + ") = " + nonTerminalSymbol.starts());
        }
        System.out.println("---------------------------------------");
        System.out.println();

        System.out.println("--------------- States ----------------");
        int i = 1;
        for(Set<LR1Item> state: gsa.getStates()){

            System.out.println("-- State " + i++ + " --");

            for(LR1Item item: state){
                System.out.println(item);
            }
            System.out.println("------------");
            System.out.println();
        }
        System.out.println("---------------------------------------");
    }

}
