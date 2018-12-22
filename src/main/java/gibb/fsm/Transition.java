package gibb.fsm;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class Transition {

    private String startState;
    private String endState;
    private String eventName;

    public Transition(String eventName, String startState, String endState) {
        this.eventName = eventName;
        this.startState = startState;
        this.endState = endState;
    }

    public void doBeforeTransition() {
    }

    public void doAfterTransition() {
    }
}
