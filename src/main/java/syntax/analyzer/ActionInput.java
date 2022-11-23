package syntax.analyzer;

import syntax.common.TerminalSymbol;

import java.io.Serializable;
import java.util.Objects;

public class ActionInput implements Serializable {

    private final int state;

    private final TerminalSymbol symbol;

    public ActionInput(int state, TerminalSymbol symbol) {
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
        ActionInput dkaInput = (ActionInput) o;
        return state == dkaInput.state && Objects.equals(symbol, dkaInput.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, symbol);
    }
}

