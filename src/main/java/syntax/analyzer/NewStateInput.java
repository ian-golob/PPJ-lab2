package syntax.analyzer;

import syntax.common.NonTerminalSymbol;

import java.io.Serializable;
import java.util.Objects;

public class NewStateInput implements Serializable {

    private final int state;

    private final NonTerminalSymbol symbol;

    public NewStateInput(int state, NonTerminalSymbol symbol) {
        this.state = state;
        this.symbol = symbol;
    }

    public int getState() {
        return state;
    }

    public NonTerminalSymbol getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewStateInput that = (NewStateInput) o;
        return state == that.state && symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, symbol);
    }
}
