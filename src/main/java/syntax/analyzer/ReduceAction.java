package syntax.analyzer;

import syntax.generator.Production;

public class ReduceAction extends Action{

    private final Production production;

    public ReduceAction(Production production) {
        super(ActionType.REDUCE);
        this.production = production;
    }

    public Production getProduction() {
        return production;
    }

    @Override
    public String toString() {
        return "r" + production.getPriority();
    }
}
