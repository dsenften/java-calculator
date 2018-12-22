package gibb.fsm;

import java.util.HashMap;
import java.util.Map;

/**
 * Stellt einen Zustand mit einer bestimmten Anzahl von zugehörigen
 * Übergängen dar.
 */
class State {

    Map<String, Transition> transitions;
    String autoTransitionState;
    Runnable entryCode;
    Runnable exitCode;
    Runnable alwaysRunCode;

    State(Runnable entryCode, Runnable exitCode, Runnable alwaysRunCode) {
        autoTransitionState = null;
        transitions = new HashMap<>();
        this.entryCode = entryCode;
        this.exitCode = exitCode;
        this.alwaysRunCode = alwaysRunCode;
    }

    void addTransition(Transition transition) {
        transitions.put(transition.getEventName(), transition);
    }

    void runEntryCode() {
        if (entryCode != null) {
            entryCode.run();
        }
    }

    void runExitCode() {
        if (exitCode != null) {
            exitCode.run();
        }
    }

    void runAlwaysCode() {
        if (alwaysRunCode != null) {
            alwaysRunCode.run();
        }
    }
}