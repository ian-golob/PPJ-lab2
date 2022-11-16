package syntax.generator;

import java.util.Objects;
import java.util.Set;

public class DKATransitionInput {

    private Set<LR1Item> state;

    private Symbol inputSymbol;

    public DKATransitionInput(Set<LR1Item> state, Symbol inputSymbol) {
        this.state = state;
        this.inputSymbol = inputSymbol;
    }

    public Set<LR1Item> getState() {
        return state;
    }

    public void setState(Set<LR1Item> state) {
        this.state = state;
    }

    public Symbol getInputSymbol() {
        return inputSymbol;
    }

    public void setInputSymbol(Symbol inputSymbol) {
        this.inputSymbol = inputSymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DKATransitionInput that = (DKATransitionInput) o;
        return state.equals(that.state) && inputSymbol.equals(that.inputSymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, inputSymbol);
    }
}
