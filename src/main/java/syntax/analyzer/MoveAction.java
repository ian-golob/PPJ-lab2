package syntax.analyzer;

public class MoveAction extends Action{

    private final int nextState;

    public MoveAction(int nextState) {
        super(ActionType.MOVE);
        this.nextState = nextState;
    }

    public int getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return "s" + nextState;
    }
}
