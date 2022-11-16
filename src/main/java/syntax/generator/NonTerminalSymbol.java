package syntax.generator;

import java.util.ArrayList;
import java.util.List;

public class NonTerminalSymbol extends Symbol {

    private final List<Production> productions = new ArrayList<>();

    public NonTerminalSymbol(String name) {
        super(name);
    }

    public List<Production> getProductions() {
        return productions;
    }

}
