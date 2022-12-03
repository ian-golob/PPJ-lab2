package syntax.analyzer;

import syntax.common.NonTerminalSymbol;
import syntax.common.Production;
import syntax.common.Symbol;

import java.io.PrintStream;
import java.util.*;

public class LRStack {
    private Stack<LRStackNode> stack;

    public LRStack(){
        stack = new Stack<>();
        stack.push(new LRStackNode(0, new NonTerminalSymbol("bottom"), -1, false));
    }

    public LRStackNode topNode(){
        return stack.peek();
    }

    public void print(PrintStream output){
        for (var el : stack){
            output.print(el.symbol + " ");
        }
        output.println();
    }

    public void move(LRStackNode node){
        stack.push(node);
    }

    public List<LRStackNode> remove(List<Symbol> rightSide) {
        boolean error = false;
        Collections.reverse(rightSide);
        List<LRStackNode> nodes = new ArrayList<>();
        if (rightSide.size() >= stack.size()) return null;
        for (Symbol symbol : rightSide) {
            LRStackNode node = stack.pop();
            nodes.add(node);
        }
        Collections.reverse(rightSide);
        Collections.reverse(nodes);
        return nodes;
    }

    public int size(){
        return stack.size();
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();

        for (LRStackNode node : stack) {
            result.append(node.symbol.toString()).append(node.state);
        }

        return result.toString();
    }
}
