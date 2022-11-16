package syntax.generator;

import java.util.List;

public abstract class SAUtil {

    /**
     * Evaluates if all the given symbols are non-terminal and empty.
     * @return True if all the given symbols are non-terminal and empty
     * @throws NullPointerException If the given symbol list is null.
     */
    public static boolean isEmptySymbolList(List<Symbol> symbols){
        if(symbols == null){
            throw new NullPointerException("The given symbol list must not be null");
        }

        for(Symbol symbol: symbols){
            if(symbol instanceof NonTerminalSymbol){
                if(!((NonTerminalSymbol) symbol).isEmptySymbol()){
                    return false;
                }

            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Evaluates if the production is an epsilon production or if all of its symbols are non-terminal and empty.
     * @return True if the production is an empty production, false othrewise.
     * @throws NullPointerException If the given production is null
     *      or if the production is not an epsilon production and its symbol list is null.
     */
    public static boolean isEmptyProduction(Production production){
        if(production == null){
            throw new NullPointerException("The given production must not be null");
        }

        if(production.isEpsilon()){
            return true;
        }

        return isEmptySymbolList(production.getRightSide());
    }

}
