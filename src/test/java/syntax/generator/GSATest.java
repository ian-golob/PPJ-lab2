package syntax.generator;

import org.junit.jupiter.api.Test;
import syntax.common.Action;
import syntax.analyzer.DKAActionInput;
import syntax.analyzer.DKANewStateInput;
import syntax.common.NonTerminalSymbol;
import syntax.common.Production;
import syntax.common.TerminalSymbol;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        System.out.println("---------------------------------------");

        System.out.println();

        /*
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
        int stateCount = 0;
        for(Set<LR1Item> state: gsa.getStates()){

            System.out.println("-- State " + stateCount++ + " --");

            for(LR1Item item: state){
                System.out.println(item);
            }
            System.out.println("------------");
            System.out.println();
        }
        System.out.println("---------------------------------------");


         */

        List<TerminalSymbol> terminalSymbolList = new ArrayList<>(gsa.getTerminalSymbols());
        terminalSymbolList.add(GSA.EOF_SYMBOL);
        List<NonTerminalSymbol> nonTerminalSymbolList = new ArrayList<>(gsa.getNonTerminalSymbols());


        int fieldLength = 6;
        System.out.print(" ".repeat(fieldLength));
        for(int j = 0; j < terminalSymbolList.size(); j ++){
            System.out.print(String.format("%" + fieldLength + "s", terminalSymbolList.get(j).toString()));
        }
        System.out.println();

        for(int i = 0; i < gsa.getStates().size(); i++){

            System.out.print(String.format("%" + fieldLength + "s", i));

            for(int j = 0; j < terminalSymbolList.size(); j ++){

                TerminalSymbol symbol = terminalSymbolList.get(j);
                DKAActionInput input = new DKAActionInput(i, symbol);

                Action action = gsa.getDkaActionTable().get(input);
                System.out.print(String.format("%" + fieldLength + "s", action));

            }
            System.out.println();
        }
        System.out.println();

        System.out.print(" ".repeat(fieldLength));
        for(int j = 0; j < nonTerminalSymbolList.size(); j ++){
            System.out.print(String.format("%" + fieldLength + "s", nonTerminalSymbolList.get(j).toString()));
        }
        System.out.println();

        for(int i = 0; i < gsa.getStates().size(); i++){

            System.out.print(String.format("%" + fieldLength + "s", i));

            for(int j = 0; j < nonTerminalSymbolList.size(); j ++){

                NonTerminalSymbol symbol = nonTerminalSymbolList.get(j);
                DKANewStateInput input = new DKANewStateInput(i, symbol);

                Integer output = gsa.getDkaNewStateTable().get(input);

                String outputString = "";
                if(output != null){
                    outputString = output.toString();
                }

                System.out.print(String.format("%" + fieldLength + "s", outputString));
            }
            System.out.println();
        }
        System.out.println();
    }

}
