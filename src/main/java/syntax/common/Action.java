package syntax.common;

import java.io.Serializable;

public abstract class Action implements Serializable {

    private final ActionType type;

    public Action(ActionType type) {
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

}
