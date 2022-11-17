package syntax.common;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Production implements Serializable {

    private final int priority;

    private final boolean isEpsilon;

    private final Symbol leftSide;

    private List<Symbol> rightSide = null;

    public Production(int priority, Symbol leftSide) {
        this.priority = priority;
        this.leftSide = leftSide;
        this.isEpsilon = true;
    }

    public Production(int priority, Symbol leftSide, List<Symbol> rightSide) {
        this.priority = priority;
        this.leftSide = leftSide;
        this.isEpsilon = false;
        this.rightSide = rightSide;
    }

    public Symbol getLeftSide() {
        return leftSide;
    }

    public List<Symbol> getRightSide() {
        return rightSide;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isEpsilon(){
        return isEpsilon;
    }

    @Override
    public String toString() {

        String rightSideString;
        if(isEpsilon){
            rightSideString = "$";
        } else {
            rightSideString = rightSide.stream().map(Symbol::toString).collect(Collectors.joining(" "));
        }

        return leftSide + " -> "
                + rightSideString + " => "
                + priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return priority == that.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priority);
    }
}
