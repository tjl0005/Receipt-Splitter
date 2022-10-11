import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ui {
    public static void displayReceipt(List<String> receipt) {
        System.out.println("This is your receipt:");
        // Print receipt line by line
        for (int i = 0; i < receipt.size(); i++) {
            System.out.println(i + " " + receipt.get(i));
        }
    }

    public static int getLineNumber() {
        System.out.println("Please select a line to modify.");
        return new Scanner(System.in).nextInt();
    }

    public static String getString(String message) {
        System.out.println(message);
        return new Scanner(System.in).next();
    }

    public static boolean askFinished() {
        // Wait until user inputs correct value
        while (true) {
            System.out.println("Have you finished? (y/n)");
            String answer = new Scanner(System.in).nextLine().toLowerCase();

            if (Objects.equals(answer, "y") | Objects.equals(answer, "yes")) {
                return true;
            } else if (Objects.equals(answer, "n") | Objects.equals(answer, "no")) {
                return false;
            } else {
                System.out.println("Please try again. ");
                askFinished(); // Attempt to get input again
            }
        }
    }
}
