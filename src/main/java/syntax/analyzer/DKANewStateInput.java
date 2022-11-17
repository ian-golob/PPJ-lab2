package syntax.analyzer;

import syntax.generator.NonTerminalSymbol;

import java.util.Objects;

public class DKANewStateInput {

    private final int state;

    private final NonTerminalSymbol symbol;

    public DKANewStateInput(int state, NonTerminalSymbol symbol) {
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
        DKANewStateInput that = (DKANewStateInput) o;
        return state == that.state && symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, symbol);
    }
}
