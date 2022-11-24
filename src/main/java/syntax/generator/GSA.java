package syntax.generator;

import syntax.analyzer.*;
import syntax.common.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GSA {

    private final Set<TerminalSymbol> terminalSymbols = new HashSet<>();

    private final Set<NonTerminalSymbol> nonTerminalSymbols = new HashSet<>();

    private final Map<String, Symbol> symbols = new HashMap<>();

    private NonTerminalSymbol firstNonTerminalSymbol;

    private final Map<DKATransitionInput, Set<LR1Item>> dkaTransitions = new HashMap<>();

    private final Map<ActionInput, Action> dkaActionTable = new HashMap<>();
    private final Map<NewStateInput, Integer> dkaNewStateTable = new HashMap<>();

    private List<Set<LR1Item>> states;

    public final static TerminalSymbol EOF_SYMBOL = new TerminalSymbol("!EOF!");

    public static void main(String... args) throws IOException {
        GSA gla = new GSA();
        gla.parseInput(System.in);
        gla.writeSAConfigObjects();
    }

    public void writeSAConfigObjects() throws IOException {

        SAConfigObject saConfigObject = new SAConfigObject(
                symbols,
                firstNonTerminalSymbol,
                dkaActionTable,
                dkaNewStateTable);
        File file = new File("./analizator/saConfigObject.obj");

        Files.createDirectories(Paths.get("analizator/"));
        file.createNewFile();

        try(ObjectOutputStream configOut =
                new ObjectOutputStream(
                        new FileOutputStream(file))){

            configOut.writeObject(saConfigObject);
        }
    }

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

        // Izracun tranzicija DKA
        calculateDKATransitions();

        // Izracun tablica Akcija i NovoStanje
        calculateDKATables();

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
                        .collect(Collectors.toList());

                Production production = new Production(priority++, leftSide, rightSide);
                leftSide.getProductions().add(production);

            }

        }

    }

    private void calculateEmptySymbols() {

        for(NonTerminalSymbol symbol: nonTerminalSymbols){
            for(Production production: symbol.getProductions()) {
                if (production.isEpsilon()) {
                    symbol.setIsEmptySymbol(true);
                }
            }
        }

        int newEmptySymbolCount;

        do{
            newEmptySymbolCount = 0;

            for(NonTerminalSymbol symbol: nonTerminalSymbols){

                if(!symbol.isEmptySymbol()){

                    for(Production production: symbol.getProductions()) {

                        if (GSAUtil.isEmptyProduction(production)) {
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

    private void calculateDKATransitions() {
        states = new ArrayList<>();
        List<Set<LR1Item>> statesToProcess = new ArrayList<>();

        Set<LR1Item> firstState = new HashSet<>();
        for(Production production: firstNonTerminalSymbol.getProductions()){
            firstState.addAll(GSAUtil.getAllEpsilonLR1Items(new LR1Item(production, Set.of(EOF_SYMBOL))));
        }
        firstState = GSAUtil.joinDuplicateLR1Items(firstState);
        statesToProcess.add(firstState);
        states.add(firstState);

        do{
            Set<LR1Item> stateToProcess = statesToProcess.get(0);
            statesToProcess.remove(0);

            Map<Symbol, Set<LR1Item>> symbolToOutput = new HashMap<>();

            for(LR1Item stateItem: stateToProcess){
                if(!stateItem.getProduction().isEpsilon() &&
                        stateItem.getDotPosition() < stateItem.getProduction().getRightSide().size()){

                    Symbol nextSymbol = stateItem.getProduction().getRightSide().get(stateItem.getDotPosition());

                    Set<LR1Item> transitionOutput = symbolToOutput.getOrDefault(nextSymbol, new HashSet<>());

                    transitionOutput.addAll(GSAUtil.getAllEpsilonLR1Items(
                            new LR1Item(stateItem.getProduction(),
                            stateItem.getFollowingSymbols(),
                            stateItem.getDotPosition() + 1)));

                    symbolToOutput.put(nextSymbol, transitionOutput);
                }
            }

            for(var entry: symbolToOutput.entrySet()){
                Symbol symbol = entry.getKey();
                Set<LR1Item> stateOutput = entry.getValue();

                stateOutput = GSAUtil.joinDuplicateLR1Items(stateOutput);

                if(!states.contains(stateOutput)){
                    statesToProcess.add(stateOutput);
                    states.add(stateOutput);
                }

                dkaTransitions.put(new DKATransitionInput(stateToProcess, symbol), stateOutput);
            }

            //DKATransitionInput transitionInput = new DKATransitionInput(stateToProcess, nextSymbol);

        } while (statesToProcess.size() != 0);

    }

    private void calculateDKATables() {
        List<TerminalSymbol> terminalSymbolList = new ArrayList<>(terminalSymbols);
        terminalSymbolList.add(EOF_SYMBOL);

        for(int i = 0; i < states.size(); i++){
            for(int j = 0; j < terminalSymbolList.size(); j ++){
                Set<LR1Item> state = states.get(i);
                TerminalSymbol symbol = terminalSymbolList.get(j);

                Action action;
                DKATransitionInput dkaTransitionInput = new DKATransitionInput(state, symbol);

                if(state.stream().anyMatch(lr1Item ->
                    lr1Item.getProduction().getLeftSide() == firstNonTerminalSymbol &&
                        (lr1Item.getProduction().isEpsilon() ||
                                lr1Item.getProduction().getRightSide().size() == lr1Item.getDotPosition()) &&
                        lr1Item.getFollowingSymbols().contains(EOF_SYMBOL) &&
                        symbol.equals(EOF_SYMBOL))){

                    action = new AcceptAction();

                } else if(dkaTransitions.containsKey(dkaTransitionInput)){

                    action = new MoveAction(states.indexOf(dkaTransitions.get(dkaTransitionInput)));

                } else {

                    Production firstReducableProduction = null;

                    for(LR1Item lr1Item: state){

                        if((lr1Item.getProduction().isEpsilon() || lr1Item.getDotPosition() == lr1Item.getProduction().getRightSide().size()) &&
                                lr1Item.getFollowingSymbols().contains(symbol) &&
                                (firstReducableProduction == null || (firstReducableProduction.getPriority() > lr1Item.getProduction().getPriority()))){
                            firstReducableProduction = lr1Item.getProduction();
                        }

                    }

                    if(firstReducableProduction != null){
                        action = new ReduceAction(firstReducableProduction);
                    } else {
                        action = new RejectAction();
                    }
                }

                ActionInput input = new ActionInput(i, symbol);
                dkaActionTable.put(input, action);
            }
        }

        List<NonTerminalSymbol> nonTerminalSymbolList = new ArrayList<>(nonTerminalSymbols);
        for(int i = 0; i < states.size(); i++){
            for(int j = 0; j < nonTerminalSymbolList.size(); j ++){
                Set<LR1Item> state = states.get(i);
                NonTerminalSymbol symbol = nonTerminalSymbolList.get(j);
                NewStateInput input = new NewStateInput(i, symbol);

                DKATransitionInput dkaTransitionInput = new DKATransitionInput(state, symbol);
                if(dkaTransitions.containsKey(dkaTransitionInput)){

                    dkaNewStateTable.put(input, states.indexOf(dkaTransitions.get(dkaTransitionInput)));

                } else {

                    dkaNewStateTable.put(input, null);

                }
            }
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

    public List<Set<LR1Item>> getStates() {
        return states;
    }

    public Map<DKATransitionInput, Set<LR1Item>> getDkaTransitions() {
        return dkaTransitions;
    }

    public Map<ActionInput, Action> getDkaActionTable() {
        return dkaActionTable;
    }

    public Map<NewStateInput, Integer> getDkaNewStateTable() {
        return dkaNewStateTable;
    }
}
