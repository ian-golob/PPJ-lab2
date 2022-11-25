package syntax.analyzer;

import syntax.common.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

            System.out.println(stack.toString());

            var action = actionTable.get(new ActionInput(currentState, currentSymbol));

            if (action.getType() == ActionType.MOVE){
                var moveAction = (MoveAction) action;
                //output.println(moveAction);
                currentState = moveAction.getNextState();
                stack.move(currentSymbol, currentState, -1);
                i++;
            } else if (action.getType() == ActionType.REDUCE){
                var reduceAction = (ReduceAction) action;
                var right = reduceAction.getProduction().getRightSide();
                //output.println(reduceAction + " " + reduceAction.getProduction().getLeftSide() + " " + right);
                var nodes = right == null ?
                        List.of(new LRStackNode(0, new TerminalSymbol("$"), -1)) :
                        stack.remove(right);
                if (nodes == null) {
                    output.print("Oporavak");
                    return;
                }
                currentState = newStateTable.get(new NewStateInput(stack.topState(), (NonTerminalSymbol) reduceAction.getProduction().getLeftSide()));
                tree.addNode(terminalSymbolCount, nodes);
                stack.move(reduceAction.getProduction().getLeftSide(), currentState, terminalSymbolCount++);

            } else if (action.getType() == ActionType.ACCEPT){
                tree.setRoot(stack.topSymbol(), terminalSymbolCount-1);
                //output.println(stack.topSymbol() + " " + (terminalSymbolCount-1));
                //tree.print(output);
                tree.traverse(output);
                break;
            }
            else{
                output.println("Syntax error");
                break;
            }
        }
    }
}
