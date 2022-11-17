package syntax.analyzer;

import syntax.generator.TerminalSymbol;

import java.io.Serializable;
import java.util.Objects;

public class DKAActionInput implements Serializable {

    private final int state;

    private final TerminalSymbol symbol;

    public DKAActionInput(int state, TerminalSymbol symbol) {
        this.state = state;
        this.symbol = symbol;
    }

    public int getState() {
        return state;
    }

    public TerminalSymbol getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DKAActionInput dkaInput = (DKAActionInput) o;
        return state == dkaInput.state && Objects.equals(symbol, dkaInput.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, symbol);
    }
}

