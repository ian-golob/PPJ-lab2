package syntax.analyzer;

import syntax.common.NonTerminalSymbol;
import syntax.common.Production;
import syntax.common.Symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

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

    public void move(Symbol symbol, int state, int index){
        stack.push(new LRStackNode(state, symbol, index));
    }

    public List<LRStackNode> remove(List<Symbol> rightSide) {
        Collections.reverse(rightSide);
        List<LRStackNode> nodes = new ArrayList<>();
        if (rightSide.size() >= stack.size()) return null;
        for (Symbol symbol : rightSide) {
            LRStackNode node = stack.pop();
            if (node.symbol != symbol) return null;
            nodes.add(node);
        }
        Collections.reverse(nodes);
        return nodes;
    }
}
