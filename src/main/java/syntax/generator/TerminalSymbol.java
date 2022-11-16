package syntax.generator;

public class TerminalSymbol extends Symbol{

    private boolean isSyncSymbol = false;

    public TerminalSymbol(String name) {
        super(name);
    }

    public boolean isSyncSymbol(){
        return isSyncSymbol;
    }

    public void setIsSyncSymbol(boolean isSyncSymbol) {
        this.isSyncSymbol = isSyncSymbol;
    }

}
