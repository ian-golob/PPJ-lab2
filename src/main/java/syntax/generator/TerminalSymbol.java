package syntax.generator;

import java.util.Set;

public class TerminalSymbol extends Symbol{

    private boolean isSyncSymbol = false;

    public TerminalSymbol(String name) {
        super(name);
    }

    @Override
    public Set<Symbol> immediatelyStartsWith() {
        return Set.of(this);
    }

    @Override
    public Set<Symbol> startsWith() {
        return Set.of(this);
    }

    @Override
    public Set<TerminalSymbol> starts() {
        return Set.of(this);
    }

    public boolean isSyncSymbol(){
        return isSyncSymbol;
    }

    public void setIsSyncSymbol(boolean isSyncSymbol) {
        this.isSyncSymbol = isSyncSymbol;
    }

}
