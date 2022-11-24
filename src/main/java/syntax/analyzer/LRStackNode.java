package syntax.analyzer;

import syntax.common.Symbol;

public class LRStackNode {
    public int state;
    public Symbol symbol;
    public int index;

    public LRStackNode(int state, Symbol symbol, int index) {
        this.state = state;
        this.symbol = symbol;
        this.index = index;
    }
}
