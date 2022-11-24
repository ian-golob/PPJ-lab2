package syntax.analyzer;

import syntax.common.Symbol;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseTree {
    private Map<Integer, List<LRStackNode>> leaves;
    private List<Line> lines;
    private Symbol rootSymbol;
    private int rootIndex;
    private int nextLineIndex = 0;

    public ParseTree(List<Line> lines) {
        this.lines = lines;
        leaves = new HashMap<>();
    }

    public void addNode(int index, List<LRStackNode> symbols){
        leaves.put(index, symbols);
    }

    public void setRoot(Symbol rootSymbol, int rootIndex) {
        this.rootSymbol = rootSymbol;
        this.rootIndex = rootIndex;
    }

    private void indent(int depth, PrintStream output){
        StringBuilder sb = new StringBuilder(depth);
        for (int i=0; i < depth; i++){
            sb.append(" ");
        }
        output.print(sb);
    }

    private void traverseNode(int index, int depth, PrintStream output){
        for (var child : leaves.get(index)){
            indent(depth, output);
            if (child.index == -1){
                output.println(lines.get(nextLineIndex++).getText());
            }
            else {
                output.println(child.symbol);
                traverseNode(child.index, depth + 1, output);
            }
        }
    }

    public void traverse(PrintStream output) {

        for (var pair : leaves.entrySet()){
            output.println(pair.getKey() + ": ");
            for (var node : pair.getValue()) {
                output.print(node.index);
                output.println(node.symbol);
            }
        }

        nextLineIndex = 0;
        output.println(rootSymbol);
        traverseNode(rootIndex, 1, output);
    }
}
