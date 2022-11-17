package syntax.common;

public class AcceptAction extends Action {
    public AcceptAction() {
        super(ActionType.ACCEPT);
    }

    @Override
    public String toString() {
        return "!";
    }
}
