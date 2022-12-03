package syntax.analyzer;

import syntax.common.Symbol;

public class LRStackNode {
    public int state;
    public Symbol symbol;
    public int index;
    public boolean isTerminal;

    public LRStackNode(int state, Symbol symbol, int index, boolean isTerminal) {
        this.state = state;
        this.symbol = symbol;
        this.index = index;
        this.isTerminal = isTerminal;
    }
}
