package syntax.analyzer;

import syntax.generator.NonTerminalSymbol;
import syntax.generator.Symbol;

import java.io.Serializable;
import java.util.Map;

public class SAConfigObject implements Serializable {

    private Map<String, Symbol> symbols;
    private NonTerminalSymbol firstNonTerminalSymbol;
    private Map<DKAActionInput, Action> dkaActionTable;
    private Map<DKANewStateInput, Integer> dkaNewStateTable;

    public SAConfigObject() {
    }

    public SAConfigObject(Map<String, Symbol> symbols, NonTerminalSymbol firstNonTerminalSymbol, Map<DKAActionInput, Action> dkaActionTable, Map<DKANewStateInput, Integer> dkaNewStateTable) {
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

    public Map<DKAActionInput, Action> getDkaActionTable() {
        return dkaActionTable;
    }

    public void setDkaActionTable(Map<DKAActionInput, Action> dkaActionTable) {
        this.dkaActionTable = dkaActionTable;
    }

    public Map<DKANewStateInput, Integer> getDkaNewStateTable() {
        return dkaNewStateTable;
    }

    public void setDkaNewStateTable(Map<DKANewStateInput, Integer> dkaNewStateTable) {
        this.dkaNewStateTable = dkaNewStateTable;
    }
}
