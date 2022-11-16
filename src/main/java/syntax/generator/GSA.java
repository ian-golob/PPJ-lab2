package syntax.generator;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class GSA {

    private final Set<TerminalSymbol> terminalSymbols = new HashSet<>();

    private final Set<NonTerminalSymbol> nonTerminalSymbols = new HashSet<>();

    private final Map<String, Symbol> symbols = new HashMap<>();

    private NonTerminalSymbol firstNonTerminalSymbol;

    private final static TerminalSymbol EOF_SYMBOL = new TerminalSymbol("!EOF!");

    public static void main(String... args) {
        GSA gla = new GSA();
        gla.parseInput(System.in);
        //gsa.writeSAConfigObjects();
    }

    /*
    public void writeSAConfigObjects() throws IOException {
        String pathPrefix = "analizator/";

        File analyzerStatesFile = new File(pathPrefix + "analyzerStates.obj");
        File lexicalElementNamesFile = new File(pathPrefix + "lexicalElementNames.obj");
        File stateToENKAListMapFile = new File(pathPrefix + "stateToENKAListMap.obj");

        analyzerStatesFile.createNewFile();
        lexicalElementNamesFile.createNewFile();
        stateToENKAListMapFile.createNewFile();

        try(ObjectOutputStream analyzerStatesFileOut = new ObjectOutputStream(new FileOutputStream(analyzerStatesFile));
            ObjectOutputStream lexicalElementNamesFileOut = new ObjectOutputStream(new FileOutputStream(lexicalElementNamesFile));
            ObjectOutputStream stateToENKAListMapFileOut = new ObjectOutputStream(new FileOutputStream(stateToENKAListMapFile))){

            analyzerStatesFileOut.writeObject(analyzerStates);
            lexicalElementNamesFileOut.writeObject(lexicalElementNames);
            stateToENKAListMapFileOut.writeObject(stateToENKAListMap);
        }
    }
     */

    public void parseInput(InputStream in) {
        Scanner sc = new Scanner(in);

        // Parsiranje nezavrsnih znakova
        inputNonTerminalSymbols(sc);

        // Parsiranje zavrsnih znakova
        inputTerminalSymbols(sc);

        // Parsiranje sinkronizacijskih znakova
        inputSyncSymbols(sc);

        // Parsiranje produkcija
        inputProductions(sc);

        // Izracun praznih simbola
        calculateEmptySymbols();

        // Izracun skupova ZapocinjeIzravnoZnakom
        calculateImmediatelyStartsWithSets();

        // Izracun skupova ZapocinjeZnakom
        calculateStartsWithSets();

        // Izracun skupova ZAPOCINJE
        calculateStartsSets();
    }

    private void inputNonTerminalSymbols(Scanner sc) {
        String[] names = sc.nextLine().split(" ");

        if(!names[0].equals("%V")){
            throw new IllegalArgumentException("\"%V\" expected");
        }

        for(int i = 1; i < names.length; i++) {
            NonTerminalSymbol symbol = new NonTerminalSymbol(names[i]);

            nonTerminalSymbols.add(symbol);
            symbols.put(names[i], symbol);
        }

        firstNonTerminalSymbol = getNonTerminalSymbol(names[1]);
    }

    private void inputTerminalSymbols(Scanner sc) {
        String[] names = sc.nextLine().split(" ");

        if(!names[0].equals("%T")){
            throw new IllegalArgumentException("\"%T\" expected");
        }

        for(int i = 1; i < names.length; i++) {
            TerminalSymbol symbol = new TerminalSymbol(names[i]);

            terminalSymbols.add(symbol);
            symbols.put(names[i], symbol);
        }
    }

    private void inputSyncSymbols(Scanner sc) {
        String[] names = sc.nextLine().split(" ");

        if(!names[0].equals("%Syn")){
            throw new IllegalArgumentException("\"%Syn\" expected");
        }

        for(int i = 1; i < names.length; i++) {
            getTerminalSymbol(names[i]).setIsSyncSymbol(true);
        }
    }

    private void inputProductions(Scanner sc) {

        String firstLeftSide = sc.nextLine();
        NonTerminalSymbol leftSide = getNonTerminalSymbol(firstLeftSide);

        int priority = 1;

        while(sc.hasNext()){

            String line = sc.nextLine();

            if(line.startsWith("<")){

                leftSide = getNonTerminalSymbol(line);

            } else if(line.startsWith(" $")){

                Production production = new Production(priority++, leftSide);
                leftSide.getProductions().add(production);

            } else {

                List<Symbol> rightSide = Arrays.stream(line.substring(1).split(" "))
                        .map(symbols::get)
                        .toList();

                Production production = new Production(priority++, leftSide, rightSide);
                leftSide.getProductions().add(production);

            }

        }

    }

    private void calculateEmptySymbols() {

        int emptySymbolCount = 0;

        for(NonTerminalSymbol symbol: nonTerminalSymbols){
            for(Production production: symbol.getProductions()) {
                if (production.isEpsilon()) {
                    symbol.setIsEmptySymbol(true);
                    emptySymbolCount++;
                }
            }
        }

        int newEmptySymbolCount;

        do{
            newEmptySymbolCount = 0;

            for(NonTerminalSymbol symbol: nonTerminalSymbols){

                if(!symbol.isEmptySymbol()){

                    for(Production production: symbol.getProductions()) {

                        if (SAUtil.isEmptyProduction(production)) {
                            symbol.setIsEmptySymbol(true);
                            newEmptySymbolCount++;
                        }

                    }

                }

            }

        } while(newEmptySymbolCount != 0);
    }

    private void calculateImmediatelyStartsWithSets() {

        if(firstNonTerminalSymbol.isEmptySymbol()){
            firstNonTerminalSymbol.immediatelyStartsWith().add(EOF_SYMBOL);
        }

        for(NonTerminalSymbol symbol: nonTerminalSymbols){
            for(Production production: symbol.getProductions()){
                if(production.isEpsilon()){
                    continue;
                }

                for(Symbol productionSymbol: production.getRightSide()){

                    symbol.immediatelyStartsWith().add(productionSymbol);

                    if(productionSymbol instanceof NonTerminalSymbol){
                        if(!((NonTerminalSymbol) productionSymbol).isEmptySymbol()){
                            break;
                        }

                    } else {
                        break;
                    }

                }
            }
        }

    }

    private void calculateStartsWithSets() {

        for(NonTerminalSymbol symbol: nonTerminalSymbols){
            symbol.startsWith().addAll(symbol.immediatelyStartsWith());
        }


        boolean someSetChanged;
        do {
            someSetChanged = false;

            for(NonTerminalSymbol symbol: nonTerminalSymbols){

                Set<Symbol> newStartsWith = new HashSet<>(symbol.startsWith());

                for(Symbol startsWithSymbol: symbol.startsWith()){

                    if(startsWithSymbol instanceof NonTerminalSymbol){
                        if(newStartsWith.addAll(startsWithSymbol.startsWith())){
                            someSetChanged = true;
                        }
                    }

                }

                if(someSetChanged){
                    symbol.startsWith().addAll(newStartsWith);
                }

            }

        } while(someSetChanged);

    }

    private void calculateStartsSets() {

        for(NonTerminalSymbol symbol: nonTerminalSymbols){
            symbol.starts().addAll(
                    symbol.startsWith()
                            .stream()
                            .filter(tmpSymbol -> tmpSymbol instanceof TerminalSymbol)
                            .map(tmpSymbol -> (TerminalSymbol) tmpSymbol)
                            .collect(Collectors.toSet()));
        }

    }

    public Set<TerminalSymbol> getTerminalSymbols() {
        return terminalSymbols;
    }

    public Set<NonTerminalSymbol> getNonTerminalSymbols() {
        return nonTerminalSymbols;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public NonTerminalSymbol getFirstNonTerminalSymbol() {
        return firstNonTerminalSymbol;
    }

    public TerminalSymbol getTerminalSymbol(String name){
        return (TerminalSymbol) symbols.get(name);
    }

    public NonTerminalSymbol getNonTerminalSymbol(String name){
        return (NonTerminalSymbol) symbols.get(name);
    }
}
