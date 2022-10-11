import java.io.IOException;
import java.util.*;
import java.util.List;

// TODO: Add option to include labels -> build on edit action
// TODO: Get integers from a line given a prefix (label)
// TODO: Basic calculator

class checker {
    public static void main(String[] args) throws IOException {
        boolean finished = false;
        List<String> receipt = new ArrayList<>(receiptFile.readReceipt("l.png"));

        while (!finished) {
            ui.displayReceipt(receipt);
            actions.performAction(receipt);
            ui.displayReceipt(receipt);
            finished = ui.askFinished();
        }

        System.out.println("Final receipt saved");
        String fileName = ui.getString("Please choose the name of the receipt file");
        receiptFile.writeToFile(receipt, fileName);
    }
}