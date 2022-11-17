package syntax.common;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public abstract class Symbol implements Serializable {

    private final String name;


    protected Symbol(String name) {
        if(name == null){
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Set<Symbol> immediatelyStartsWith();

    public abstract Set<Symbol> startsWith();

    public abstract Set<TerminalSymbol> starts();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(name, symbol.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

}
