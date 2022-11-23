package syntax.analyzer;

import syntax.common.Action;
import syntax.common.NonTerminalSymbol;
import syntax.common.Symbol;
import syntax.common.TerminalSymbol;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

public class SA {

    public final static TerminalSymbol EOF_SYMBOL = new TerminalSymbol("!EOF!");

    // Config objects
    private Map<String, Symbol> symbols;
    private NonTerminalSymbol firstNonTerminalSymbol;
    private Map<ActionInput, Action> actionTable;
    private Map<NewStateInput, Integer> newStateTable;


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SA sa = new SA();
        sa.readSAConfigObject();

        sa.analyzeInput(System.in, System.out);
    }

    public void readSAConfigObject() throws IOException, ClassNotFoundException {
        Path path = Path.of("analizator/saConfigObject.obj");

        try(ObjectInputStream configIn = new ObjectInputStream(new FileInputStream(path.toFile()))){

            SAConfigObject saConfigObject =(SAConfigObject) configIn.readObject();

            symbols = saConfigObject.getSymbols();
            firstNonTerminalSymbol = saConfigObject.getFirstNonTerminalSymbol();
            actionTable = saConfigObject.getDkaActionTable();
            newStateTable = saConfigObject.getDkaNewStateTable();

        }
    }

    public void analyzeInput(InputStream input, PrintStream output) {
        //TODO
    }
}
