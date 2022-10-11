import java.util.List;
import java.util.Scanner;

public class actions {
    public static void performAction(List<String> receipt) {
    int index = ui.getLineNumber();

    switch (ui.getString("What would you like to modify.").toLowerCase()) {
        case "edit" -> editLine(receipt, index);
        case "delete" -> deleteLine(receipt, index);
        case "add" -> addLine(receipt, index);
        default -> System.out.println("Invalid selection. ");
        }
    }

    public static void editLine(List<String> receipt, int index) {
        System.out.println("Please input the desired text for this line.");
        receipt.set(index, new Scanner(System.in).nextLine());
    }

    public static void deleteLine(List<String> receipt, int index) {
        receipt.remove(index);
    }

    public static void addLine(List<String> receipt, int index) {
        System.out.println("Please input the desired text for this line.");
        receipt.add(index, new Scanner(System.in).nextLine());
    }
}
