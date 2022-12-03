package syntax.analyzer;

import syntax.common.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SA {

    public final TerminalSymbol EOF_SYMBOL = new TerminalSymbol("!EOF!");

    // Config objects
    private Map<String, Symbol> symbols;
    private NonTerminalSymbol firstNonTerminalSymbol;
    private Map<ActionInput, Action> actionTable;
    private Map<NewStateInput, Integer> newStateTable;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SA sa = new SA();
        sa.readSAConfigObject("analizator/saConfigObject.obj");

        sa.analyzeInput(System.in, System.out);
    }

    public void readSAConfigObject(String pathString) throws IOException, ClassNotFoundException {
        Path path = Path.of(pathString);

        try(ObjectInputStream configIn = new ObjectInputStream(new FileInputStream(path.toFile()))){

            SAConfigObject saConfigObject =(SAConfigObject) configIn.readObject();

            symbols = saConfigObject.getSymbols();
            firstNonTerminalSymbol = saConfigObject.getFirstNonTerminalSymbol();
            actionTable = saConfigObject.getDkaActionTable();
            newStateTable = saConfigObject.getDkaNewStateTable();

        }
    }

    public void analyzeInput(InputStream input, PrintStream output) {
        List<String> inputLines = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
        List<Line> lines = inputLines.stream().map(line -> new Line(line, symbols)).collect(Collectors.toList());
        LRStack stack = new LRStack();
        int terminalSymbolCount = 0;
        ParseTree tree = new ParseTree(lines);

        int i = 0;
        TerminalSymbol currentSymbol;
        int currentState = 0;

        while (true) {
            if (i < lines.size()) {
                currentSymbol = lines.get(i).getSymbol();
            } else currentSymbol = EOF_SYMBOL;

            //stack.print(output);
            //output.println(currentState);

            //System.out.println(stack.toString());

            var action = actionTable.get(new ActionInput(currentState, currentSymbol));

            if (action.getType() == ActionType.MOVE){
                var moveAction = (MoveAction) action;
                //output.println(moveAction);
                currentState = moveAction.getNextState();
                stack.move(new LRStackNode(currentState, currentSymbol, i, true));
                i++;
            } else if (action.getType() == ActionType.REDUCE){
                var reduceAction = (ReduceAction) action;
                var right = reduceAction.getProduction().getRightSide();
                //output.println(reduceAction + " " + reduceAction.getProduction().getLeftSide() + " " + right);
                var nodes = right == null ?
                        List.of(new LRStackNode(0, new TerminalSymbol("$"), -1, true)) :
                        stack.remove(right);
                currentState = newStateTable.get(new NewStateInput(stack.topNode().state, (NonTerminalSymbol) reduceAction.getProduction().getLeftSide()));
                tree.addNode(nodes);
                stack.move(new LRStackNode(currentState, reduceAction.getProduction().getLeftSide(), terminalSymbolCount++, false));

            } else if (action.getType() == ActionType.ACCEPT){
                tree.setRoot(stack.topNode().symbol, terminalSymbolCount-1);
                //output.println(stack.topSymbol() + " " + (terminalSymbolCount-1));
                //tree.print(output);
                tree.traverse(output);
                break;
            }
            else{
                System.out.println("Sintaksna greška u liniji " + (i+1));
                System.out.print("Očekivani uniformni znakovi: ");
                for (Symbol symbol : symbols.values()) {
                    if (symbol instanceof TerminalSymbol &&
                            actionTable.get(new ActionInput(currentState, (TerminalSymbol) symbol)).getType() != ActionType.REJECT){
                        System.out.print(symbol + " ");
                    }
                }
                System.out.println();
                System.out.println("Pročitani uniformni znak: " + currentSymbol);
                System.out.println("Odgovarajući izvorni kod uniformong znaka: " + lines.get(i).getText());

                while (i < lines.size() && !(currentSymbol = (TerminalSymbol) lines.get(i).getSymbol()).isSyncSymbol()){
                    tree.addLineSkip(i++);
                }
                if (i >= lines.size()) currentSymbol = EOF_SYMBOL;
                while (actionTable.get(new ActionInput(currentState, currentSymbol)).getType() == ActionType.REJECT){
                    var topNode = stack.topNode();
                    if (topNode.isTerminal) tree.addLineSkip(topNode.index);
                    stack.remove(List.of(topNode.symbol));
                    currentState = stack.topNode().state;
                }
            }
        }
    }
}
