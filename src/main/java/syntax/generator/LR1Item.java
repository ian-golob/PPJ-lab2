package syntax.generator;

import syntax.common.Production;
import syntax.common.TerminalSymbol;

import java.util.Objects;
import java.util.Set;

public class LR1Item {

    private final Production production;

    private int dotPosition = 0;

    private final Set<TerminalSymbol> followingSymbols;

    public LR1Item(Production production, Set<TerminalSymbol> followingSymbols) {
        this.production = production;
        this.followingSymbols = followingSymbols;
    }

    public LR1Item(Production production, Set<TerminalSymbol> followingSymbols, int dotPosition) {
        this.production = production;
        this.followingSymbols = followingSymbols;
        this.dotPosition = dotPosition;
    }

    public int getDotPosition() {
        return dotPosition;
    }

    public void setDotPosition(int dotPosition) {
        this.dotPosition = dotPosition;
    }

    public Production getProduction() {
        return production;
    }

    public Set<TerminalSymbol> getFollowingSymbols() {
        return followingSymbols;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR1Item lr1Item = (LR1Item) o;
        return dotPosition == lr1Item.dotPosition &&
                production.equals(((LR1Item) o).production) &&
                followingSymbols.equals(((LR1Item) o).followingSymbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(production, dotPosition, followingSymbols);
    }

    @Override
    public String toString() {

        String rightSideString = "";
        if(production.isEpsilon()){
            rightSideString = "[x]";
        } else {
            for(int i = 0; i < production.getRightSide().size(); i++){
                if(dotPosition == i){
                    rightSideString+=" [x]";
                }

                rightSideString += " " + production.getRightSide().get(i);
            }

            if(dotPosition == production.getRightSide().size()){
                rightSideString += " [x]";
            }
        }

        return production.getLeftSide() + " -> "
                + rightSideString+ ", "
                + followingSymbols.toString();
    }
}
