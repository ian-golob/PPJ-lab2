package syntax.analyzer;

import syntax.common.Symbol;
import syntax.common.TerminalSymbol;

import java.util.Map;

public class Line{
    private int number;
    private String text;
    private TerminalSymbol symbol;

    public Line(String line, Map<String, Symbol> symbols) {
        int firstSpace = line.indexOf(' ');
        int secondSpace = firstSpace + 1 + line.substring(firstSpace + 1).indexOf(' ');
        symbol = (TerminalSymbol) symbols.get(line.substring(0, firstSpace));
        number = Integer.parseInt(line.substring(firstSpace + 1, secondSpace));
        text = line;
    }

    public int getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    public TerminalSymbol getSymbol() {
        return symbol;
    }
}