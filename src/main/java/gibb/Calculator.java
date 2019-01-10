package gibb;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Dies ist das initiale Gerüst für unseren Rechner. Das Programm selber
 * soll eine Rechnug auf der Kommandozeile verarbeiten, in der Form:
 *
 * <code>
 * $ gibb.FSMCalculator 123.4 + 56.7
 * 180.1
 *
 * </code>
 * <p>
 * In dieser Version verwenden wir die vorhandene Scanner() Klasse.
 */
public class Calculator {

    private static final String FLOAT = "[0-9]+(\\.[0-9]*)?";
    private static final String OPERATOR = "[\\+\\-]";

    public static void main(String[] args) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File("src/main/resources/ScannerInput.txt"));

        if (scanner.hasNext()) {

            float f1 = Float.valueOf(scanner.next(FLOAT));

            while (scanner.hasNext()) {

                String operator = scanner.next(OPERATOR);
                float f2 = Float.valueOf(scanner.next(FLOAT));

                switch (operator) {
                    case "+":
                        f1 += f2;
                        break;
                    case "-":
                        f1 -= f2;
                        break;
                }

            }

            System.out.println("Resultat: " + f1);

        }
    }
}
