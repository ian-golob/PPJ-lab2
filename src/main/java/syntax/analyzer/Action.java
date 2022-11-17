package syntax.analyzer;

public abstract class Action {

    private final ActionType type;

    public Action(ActionType type) {
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

}
