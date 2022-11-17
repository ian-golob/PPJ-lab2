package syntax.analyzer;

public class RejectAction extends Action{

    public RejectAction() {
        super(ActionType.REJECT);
    }

    @Override
    public String toString() {
        return "-";
    }

}
