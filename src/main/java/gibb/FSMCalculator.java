package gibb;

import gibb.fsm.FSM;
import gibb.fsm.Transition;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dies ist das initiale Gerüst für unseren Rechner. Das Programm selber
 * soll eine Rechnug auf der Kommandozeile verarbeiten, in der Form:
 *
 * <code>
 * $ gibb.FSMCalculator 123.4 + 56.7
 * 180.1
 *
 * </code>
 */
public class FSMCalculator {

    private FSM fsm = new FSM("FSMCalculator");
    private Stack<Double> stack = new Stack<>();
    private String currentSubString;
    private String currentOperator = "";

    private static final String[] patterns = {
            "[0-9]+(\\.[0-9]*)?",
            "[+*/\\-]",
            "[0-9]+(\\.[0-9]*)?"};


    /**
     * Initialisieren der FSM mit all ihren Stati und Statusübergänge.
     */
    private void initStateMachine() {

        fsm.addState("Start");
        fsm.addState("Number", null, null,
                () -> {
                    if (!currentSubString.matches(patterns[1])) {
                        stack.push(Double.valueOf(currentSubString));
                        System.out.println("Number: " + stack.peek());
                    }
                });
        fsm.addState("Operator", null, null,
                () -> System.out.println("Operator: " + currentSubString));
        fsm.addState("End");

        fsm.addTransition(new Transition("readNumbers", "Start", "Number"));
        fsm.addTransition(new Transition("readNumbers", "Number", "Number"));
        fsm.addTransition(new Transition("readOperator", "Number", "Operator"));

        fsm.setAutoTransition("Operator", "Number");
    }


    private Double getResult() {
        double result = 0d;
        switch (currentOperator) {
            case "+": {
                result = stack.pop() + stack.pop();
                break;
            }
            case "-": {
                Double d2 = stack.pop();
                Double d1 = stack.pop();
                result = d1 - d2;
                break;
            }
            case "*": {
                result = stack.pop() * stack.pop();
                break;
            }
            case "/": {
                Double d2 = stack.pop();
                Double d1 = stack.pop();
                result = d1 / d2;
                break;
            }
        }
        return stack.push(result);
    }

    public static void main(String[] args) {

        FSMCalculator calculator = new FSMCalculator();
        calculator.initStateMachine();

        StringBuilder builder = new StringBuilder();
        for (String string : args) {
            builder.append(string);
        }

        String line = builder.toString();

        int i = 0;
        while (i < 3 && line.length() > 0) {

            Pattern pattern = Pattern.compile(patterns[i]);
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {

                calculator.currentSubString = matcher.group();
                if (!calculator.currentSubString.matches(patterns[1])) {
                    calculator.fsm.addEvent("readNumbers");
                } else {
                    calculator.currentOperator = calculator.currentSubString;
                    calculator.fsm.addEvent("readOperator");
                }

                line = line.substring(matcher.end());
            }

            i++;
        }

        System.out.println("Result: " + calculator.getResult());

    }

}
