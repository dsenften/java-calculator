package gibb.fsm;

import lombok.Getter;
import lombok.Setter;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FSM {

    private final Logger LOG = Logger.getLogger("FSM");

    private String name;
    private Map<String, State> states;

    @Getter
    private String currentState;

    private List<ChangeListener> changeListeners;

    @Setter
    private boolean debug;

    /**
     * Erstellt eine Finite State Machine (FSM) mit einem willkürlichen
     * Namen.
     */
    public FSM(String name) {
        this.name = name;
        this.states = new HashMap<String, State>();
        this.currentState = null;
        this.changeListeners = new ArrayList<ChangeListener>();
    }

    /**
     * Fügt einen neuen Zustand ohne Ein- oder Ausgangscode hinzu.
     */
    public void addState(String state) {
        addState(state, null, null, null);
    }

    /**
     * Einen neuen Zustand einrichten, von dem die FSM Kenntnis hat. Wenn die
     * FSM derzeit keine Zustände hat, dann wird dieser Zustand zum
     * Ausgangszustand (initial state).
     * <p>
     * entryCode, exitCode und alwaysRunCode sind Runnables, welche beim
     * Übergang von einem zum nächsten Zustand ausgeführt werden. entryCode
     * und exitCode werden nur ausgeführt, wenn der Übergang zwei unterschiedliche
     * Stati betrifft (z.B. A->B, wobei A != B). Im Gegenzug wird alwaysRunCode
     * in jedem Fall ausgeführt (auch dann, wenn A = B)
     */
    public void addState(String state, Runnable entryCode, Runnable exitCode,
                         Runnable alwaysRunCode) {
        boolean isInitial = (states.size() == 0);
        if (!states.containsKey(state)) {
            states.put(state, new State(entryCode, exitCode, alwaysRunCode));
        }
        if (isInitial) {
            setState(state);
        }
    }

    public void setStateEntryCode(String state, Runnable entryCode) {
        states.get(state).entryCode = entryCode;
    }

    public void setStateExitCode(String state, Runnable exitCode) {
        states.get(state).exitCode = exitCode;
    }

    public void setStateAlwaysRunCode(String state, Runnable alwaysRunCode) {
        states.get(state).alwaysRunCode = alwaysRunCode;
    }

    /**
     * Es gibt Fälle, in denen ein Zustand als Übergangszustand gedacht ist.
     * In diesem Fall sollte die FSM immer sofort in einen anderen Zustand
     * übergehen. In diesen Fällen kann diese Methode verwendet werden,
     * um den Start- und Endzustand festzulegen.
     * <p>
     * Nachdem der startState vollständig bearbeitet wurde (und alle
     * Änderungsereignisse ausgelöst wurden), überprüft die FSM, ob ein
     * automatischer Übergang ausgelöst werden sollte.
     * <p>
     * Diese Methode erstellt einen speziellen Übergang in der Lookup-Tabelle
     * namens "(auto)".
     */
    public void setAutoTransition(String startState, String endState) {
        if (debug) {
            LOG.log(Level.INFO,
                    "Establishing auto transition for " +
                            startState + " -> " + endState);
        }
        states.get(startState).autoTransitionState = endState;
        addTransition(new Transition("(auto)", startState, endState));
    }

    /**
     * Setzt den aktuellen Zustand, ohne einem Übergang zu folgen.
     * Dies führt dazu, dass ein Änderungsereignis ausgelöst wird.
     */
    public void setState(String state) {
        setState(state, true);
    }

    /**
     * Setzt den aktuellen Zustand, ohne einer Transition zu folgen und
     * optional ein Änderungsereignis auszulösen. Bei Zustandsübergängen
     * (mit der Methode addEvent) wird diese Methode verwendet, wobei
     * der Parameter triggerEvent falsch gesetzt wird.
     * <p>
     * Der FSM führt nicht-null Runnables nach der folgenden Logik aus,
     * die den Start- und Endzuständen A und B entspricht:
     *
     * <ol>
     * <li>Wenn A und B unterschiedlich sind, wird der Exit-Code von
     * A ausgeführt.</li>
     * <l>Der aktuelle Zustand wird als B definiert.</l>
     * <li>Der Code "alwaysRunCode" von B wird ausgeführt</li>
     * <l>Wenn A und B unterschiedlich sind, dann wird der entryCode
     * von B ausgeführt.</l>
     * </ol>
     */
    public void setState(String state, boolean triggerEvent) {
        boolean runExtraCode = !state.equals(currentState);
        if (runExtraCode && currentState != null) {
            states.get(currentState).runExitCode();
        }
        currentState = state;
        states.get(currentState).runAlwaysCode();
        if (runExtraCode) {
            states.get(currentState).runEntryCode();
        }
        if (triggerEvent) {
            fireChangeEvent();
        }
    }

    /**
     * Einen neuen Übergang definieren.
     */
    public void addTransition(Transition trans) {
        State st = states.get(trans.getStartState());
        if (st == null) {
            throw new NoSuchElementException("Missing state: "
                    + trans.getStartState());
        }
        st.addTransition(trans);
    }

    /**
     * Dies ist ein Standard Java Change Listener und wird nur verwendet,
     * um bereits eingetretene Änderungen zu melden. ChangeEvents werden nur
     * ausgelöst, nachdem die doAfterTransition eines Übergangs aufgerufen wurde.
     */
    public void addChangeListener(ChangeListener changeListener) {
        if (!changeListeners.contains(changeListener)) {
            changeListeners.add(changeListener);
        }
    }

    /**
     * Füttern der FSM mit dem genannten Ereignis.
     * <p>
     * Wenn der aktuelle Zustand einen Übergang hat, der auf das
     * gegebene Ereignis reagiert, führt der FSM den Übergang mit
     * den folgenden Schritten durch, wobei er davon ausgeht, dass
     * die Start- und Endzustände A und B sind:
     *
     * <ol>
     *     <li>Ausführen der "doBeforeTransition"-Methode des Übergangs.</li>
     *     <li>Setzen des neuen Zustandes "fsm.setState(B)".</li>
     *     <li>Ausführen der "doAfterTransition"-Methode des Übergangs.</li>
     *     <li>Ein Änderungsereignis auslösen und interessierte Beobachter
     *     darüber informieren, dass der Übergang abgeschlossen ist.</li>
     *     <li>Jetzt im Zustand B sehen, ob B einen dritten Zustand C hat,
     *     auf den wir automatisch über addEvent(C) wechseln müssen.</li>
     * </ol>
     */
    public void addEvent(String evtName) {
        State state = states.get(currentState);
        if (state.transitions.containsKey(evtName)) {
            Transition trans = state.transitions.get(evtName);
            if (debug) {
                LOG.log(Level.INFO,
                        "Event: " + evtName + ", " + trans.getStartState() +
                                " --> " + trans.getEndState());
            }
            trans.doBeforeTransition();
            setState(trans.getEndState(), false);
            trans.doAfterTransition();
            fireChangeEvent();
            if (states.get(trans.getEndState()).autoTransitionState != null) {
                if (debug) {
                    LOG.log(Level.INFO,
                            "FSM", "Automatically transitioning from " +
                                    trans.getEndState() + " to "
                                    + states.get(trans.getEndState()).autoTransitionState);
                }
                addEvent("(auto)");
            }
        }
    }

    /**
     * Ein Änderungsereignis an registrierte Zuhörer senden.
     */
    private void fireChangeEvent() {
        ChangeEvent changeEvent = new ChangeEvent(this);
        for (ChangeListener changeListener : changeListeners) {
            changeListener.stateChanged(changeEvent);
        }
    }

}
