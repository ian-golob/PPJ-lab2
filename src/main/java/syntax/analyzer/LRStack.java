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
        stack.push(new LRStackNode(0, new NonTerminalSymbol("bottom"), -1));
    }

    public int topState(){
        return stack.peek().state;
    }

    public Symbol topSymbol(){
        return stack.peek().symbol;
    }

    public void print(PrintStream output){
        for (var el : stack){
            output.print(el.symbol + " ");
        }
        output.println();
    }

    public void move(Symbol symbol, int state, int index){
        stack.push(new LRStackNode(state, symbol, index));
    }

    public List<LRStackNode> remove(List<Symbol> rightSide) {
        Collections.reverse(rightSide);
        List<LRStackNode> nodes = new ArrayList<>();
        if (rightSide.size() >= stack.size()) return null;
        for (Symbol symbol : rightSide) {
            LRStackNode node = stack.pop();
            if (!node.symbol.getName().equals(symbol.getName())) return null;
            nodes.add(node);
        }
        Collections.reverse(rightSide);
        Collections.reverse(nodes);
        return nodes;
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
