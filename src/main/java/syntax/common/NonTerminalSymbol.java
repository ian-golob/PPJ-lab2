package syntax.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NonTerminalSymbol extends Symbol {

    private boolean isEmptySymbol = false;

    private final List<Production> productions = new ArrayList<>();

    private final Set<Symbol> immediatelyStartsWithSet = new HashSet<>();

    private final Set<Symbol> startsWithSet = new HashSet<>();

    private final Set<TerminalSymbol> starts = new HashSet<>();

    public NonTerminalSymbol(String name) {
        super(name);
        immediatelyStartsWithSet.add(this);
        startsWithSet.add(this);
    }

    @Override
    public Set<Symbol> immediatelyStartsWith() {
        return immediatelyStartsWithSet;
    }

    @Override
    public Set<Symbol> startsWith() {
        return startsWithSet;
    }

    @Override
    public Set<TerminalSymbol> starts() {
        return starts;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public boolean isEmptySymbol(){
        return isEmptySymbol;
    }

    public void setIsEmptySymbol(boolean isEmptySymbol){
        this.isEmptySymbol = isEmptySymbol;
    }

}
