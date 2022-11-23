package syntax.analyzer;

import syntax.common.Action;
import syntax.common.NonTerminalSymbol;
import syntax.common.Symbol;

import java.io.Serializable;
import java.util.Map;

public class SAConfigObject implements Serializable {

    private Map<String, Symbol> symbols;
    private NonTerminalSymbol firstNonTerminalSymbol;
    private Map<ActionInput, Action> dkaActionTable;
    private Map<NewStateInput, Integer> dkaNewStateTable;

    public SAConfigObject() {
    }

    public SAConfigObject(Map<String, Symbol> symbols, NonTerminalSymbol firstNonTerminalSymbol, Map<ActionInput, Action> dkaActionTable, Map<NewStateInput, Integer> dkaNewStateTable) {
        this.symbols = symbols;
        this.firstNonTerminalSymbol = firstNonTerminalSymbol;
        this.dkaActionTable = dkaActionTable;
        this.dkaNewStateTable = dkaNewStateTable;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    public NonTerminalSymbol getFirstNonTerminalSymbol() {
        return firstNonTerminalSymbol;
    }

    public void setFirstNonTerminalSymbol(NonTerminalSymbol firstNonTerminalSymbol) {
        this.firstNonTerminalSymbol = firstNonTerminalSymbol;
    }

    public Map<ActionInput, Action> getDkaActionTable() {
        return dkaActionTable;
    }

    public void setDkaActionTable(Map<ActionInput, Action> dkaActionTable) {
        this.dkaActionTable = dkaActionTable;
    }

    public Map<NewStateInput, Integer> getDkaNewStateTable() {
        return dkaNewStateTable;
    }

    public void setDkaNewStateTable(Map<NewStateInput, Integer> dkaNewStateTable) {
        this.dkaNewStateTable = dkaNewStateTable;
    }
}
