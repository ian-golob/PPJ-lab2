package syntax.analyzer;

import syntax.common.Symbol;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ParseTree {
    private List<List<LRStackNode>> children;
    private List<Line> lines;
    private Set<Integer> lineSkips;
    private Symbol rootSymbol;
    private int rootIndex;
    private int nextLineIndex = 0;

    public ParseTree(List<Line> lines) {
        this.lines = lines;
        children = new ArrayList<>();
        lineSkips = new TreeSet<>();
    }

    public void print(PrintStream output){
        for (int i = 0; i < children.size(); i++){
            for (var leaf : children.get(i)){
                output.print(leaf.symbol + " " + leaf.index + " ");
            }
            output.println();
        }
    }

    public void addNode(List<LRStackNode> symbols){
        children.add(symbols);
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

    public void addLineSkip(int i){
        lineSkips.add(i);
    }

    private void traverseNode(int index, int depth, PrintStream output){
        for (var child : children.get(index)){
            indent(depth, output);
            if (child.symbol.getName().equals("$")) output.println("$");
            else if (child.isTerminal){
                while (lineSkips.contains(nextLineIndex)) nextLineIndex++;
                var line = lines.get(nextLineIndex);
                output.println(line.getSymbol() + " " + line.getNumber() + " " + line.getText());
                nextLineIndex++;
            }
            else {
                output.println(child.symbol);
                traverseNode(child.index, depth + 1, output);
            }
        }
    }

    public void traverse(PrintStream output) {
        //output.println(lineSkips);
        nextLineIndex = 0;
        output.println(rootSymbol);
        traverseNode(rootIndex, 1, output);
    }
}
