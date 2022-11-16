package syntax.generator;

import java.util.*;
import java.util.stream.Collectors;

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

    public static Set<TerminalSymbol> getStartsSet(List<Symbol> symbols){
        Set<TerminalSymbol> startsSet = new HashSet<>();

        for(Symbol symbol: symbols){
            startsSet.addAll(symbol.starts());

            if(symbol instanceof TerminalSymbol ||
                    (symbol instanceof NonTerminalSymbol &&
                            !((NonTerminalSymbol) symbol).isEmptySymbol())){
                break;
            }
        }

        return startsSet;
    }

    public static Set<TerminalSymbol> getNextFollowingTerminalSymbolSet(LR1Item lr1Item){
        Set<TerminalSymbol> followingSet = new HashSet<>();

        int calculationStart = lr1Item.getDotPosition() + 1;

        if(lr1Item.getProduction().isEpsilon() ||
                calculationStart == lr1Item.getProduction().getRightSide().size()
                || isEmptySymbolList(lr1Item.getProduction().getRightSide().subList(
                calculationStart,
                lr1Item.getProduction().getRightSide().size()))){
            followingSet.addAll(lr1Item.getFollowingSymbols());
        }

        if(calculationStart < lr1Item.getProduction().getRightSide().size()){
            followingSet.addAll(getStartsSet(lr1Item.getProduction().getRightSide().subList(
                    calculationStart,
                    lr1Item.getProduction().getRightSide().size())));
        }

        return followingSet;
    }

    /**
     * Calculates the set of all LR1Items connected with an epsilon transition from the lr1Item (directly and indirectly).
     * @param lr1Item The LR1 Item.
     * @return the set of all LR1Items connected with an epsilon transition from the lr1Item (directly and indirectly).
     */
    public static Set<LR1Item> getAllEpsilonLR1Items(LR1Item lr1Item){
        Set<LR1Item> items = new HashSet<>();
        Set<LR1Item> itemsToProcess = new HashSet<>();
        itemsToProcess.add(lr1Item);

        do{
            LR1Item item = itemsToProcess.stream().findAny().get();
            itemsToProcess.remove(item);
            items.add(item);

            if(item.getProduction().isEpsilon()){
                continue;
            }

            if(item.getDotPosition() < item.getProduction().getRightSide().size()
                    && item.getProduction().getRightSide().get(item.getDotPosition()) instanceof NonTerminalSymbol){

                Set<TerminalSymbol> followingSet = getNextFollowingTerminalSymbolSet(item);

                NonTerminalSymbol nonTerminalSymbol = (NonTerminalSymbol) item.getProduction()
                        .getRightSide().get(item.getDotPosition());

                for(Production production: nonTerminalSymbol.getProductions()){
                    LR1Item newItem = new LR1Item(production, followingSet);

                    if(!items.contains(newItem)){
                        itemsToProcess.add(newItem);
                    }

                }
            }
        } while(itemsToProcess.size() != 0);

        return joinDuplicateLR1Items(items);
    }

    public static Set<LR1Item> joinDuplicateLR1Items(Set<LR1Item> stateWithDuplicateItems) {

        class ProductionDotPositionPair {

            private final Production production;

            private final int dotPosition;

            public ProductionDotPositionPair(LR1Item item) {
                this.production = item.getProduction();
                this.dotPosition = item.getDotPosition();
            }

            public Production getProduction() {
                return production;
            }

            public int getDotPosition() {
                return dotPosition;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ProductionDotPositionPair that = (ProductionDotPositionPair) o;
                return dotPosition == that.dotPosition && production.equals(that.production);
            }

            @Override
            public int hashCode() {
                return Objects.hash(production, dotPosition);
            }
        }

        Map<ProductionDotPositionPair, Set<TerminalSymbol>> lr1ItemMap = new HashMap<>();

        for(LR1Item item: stateWithDuplicateItems){

            ProductionDotPositionPair pair = new ProductionDotPositionPair(item);

            Set<TerminalSymbol> symbols = lr1ItemMap.getOrDefault(pair, new HashSet<>());
            symbols.addAll(item.getFollowingSymbols());
            lr1ItemMap.put(pair, symbols);
        }

        return lr1ItemMap.entrySet()
                .stream()
                .map(entry ->
                        new LR1Item(entry.getKey().getProduction(),
                                entry.getValue(),
                                entry.getKey().getDotPosition()))
                .collect(Collectors.toSet());
    }
}
