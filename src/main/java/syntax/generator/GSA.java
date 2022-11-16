package syntax.generator;

import java.io.InputStream;
import java.util.*;

public class GSA {

    private Map<String, TerminalSymbol> terminalSymbols;

    private Map<String, NonTerminalSymbol> nonTerminalSymbols;

    private Map<String, Symbol> symbols;

    private NonTerminalSymbol firstNonTerminalSymbol;

    public static void main(String... args) {
        GSA gla = new GSA();
        gla.parseInput(System.in);
        //gla.writeLAConfigObjects();
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

        inputNonTerminalSymbols(sc);

        inputTerminalSymbols(sc);

        inputSyncSymbols(sc);

        symbols = new HashMap<>();

        symbols.putAll(nonTerminalSymbols);
        symbols.putAll(terminalSymbols);
        
        inputProductions(sc);

        for(NonTerminalSymbol nonTerminalSymbol: nonTerminalSymbols.values()){
            for(Production p: nonTerminalSymbol.getProductions()){
                System.out.println(p);
            }
        }
    }

    private void inputNonTerminalSymbols(Scanner sc) {
        String[] names = sc.nextLine().split(" ");

        if(!names[0].equals("%V")){
            throw new IllegalArgumentException("\"%V\" expected");
        }

        nonTerminalSymbols = new HashMap<>();

        for(int i = 1; i < names.length; i++) {
            nonTerminalSymbols.put(names[i], new NonTerminalSymbol(names[i]));
        }

        firstNonTerminalSymbol = nonTerminalSymbols.get(names[1]);
    }

    private void inputTerminalSymbols(Scanner sc) {
        String[] names = sc.nextLine().split(" ");

        if(!names[0].equals("%T")){
            throw new IllegalArgumentException("\"%T\" expected");
        }

        terminalSymbols = new HashMap<>();

        for(int i = 1; i < names.length; i++) {
            terminalSymbols.put(names[i], new TerminalSymbol(names[i]));
        }
    }

    private void inputSyncSymbols(Scanner sc) {
        String[] names = sc.nextLine().split(" ");

        if(!names[0].equals("%Syn")){
            throw new IllegalArgumentException("\"%Syn\" expected");
        }

        for(int i = 1; i < names.length; i++) {
            terminalSymbols.get(names[i]).setIsSyncSymbol(true);
        }
    }

    private void inputProductions(Scanner sc) {

        String firstLeftSide = sc.nextLine();
        NonTerminalSymbol leftSide = nonTerminalSymbols.get(firstLeftSide);

        int priority = 1;

        while(sc.hasNext()){

            String line = sc.nextLine();

            if(line.startsWith("<")){

                leftSide = nonTerminalSymbols.get(line);

            } else if(line.startsWith(" $")){

                Production production = new Production(priority++, leftSide);
                leftSide.getProductions().add(production);

            } else {

                List<Symbol> rightSide = Arrays.stream(line.substring(1).split(" "))
                        .map(name ->
                            symbols.get(name))
                        .toList();

                Production production = new Production(priority++, leftSide, rightSide);
                leftSide.getProductions().add(production);

            }

        }

    }

}
