package receipt;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Track {
    // Lists to store upto 5 changes
    public static List<List<String>> undoTracker = new ArrayList<>();
    public static List<List<String>> redoTracker = new ArrayList<>();

    public static void recordChange(DefaultListModel<String> receipt){
        // Only tracking upto 5 changes
        if (undoTracker.size() > 5) {
            undoTracker.remove(0);
        }
        undoTracker.add(getContents(receipt));
    }

    public static void undo(DefaultListModel<String> receipt){
        redoTracker.add(getContents(receipt));
        receipt.clear();
        receipt.addAll(undoTracker.get(undoTracker.size() - 1)); // Get the newest change and update model with it
        undoTracker.remove(undoTracker.size() - 1); // Remove this change from the tracker
    }

    public static void redo(DefaultListModel<String> receipt){
        undoTracker.add(getContents(receipt));
        receipt.clear();
        receipt.addAll(redoTracker.get(redoTracker.size() - 1)); // Get the newest change and update model with it
        redoTracker.remove(redoTracker.size() - 1); // Remove this change from the tracker
    }

    public static List<String> getContents(DefaultListModel<String> receipt){
        List<String> receiptCopy = new ArrayList<>();
        for (int i = 0; i < receipt.size(); i++){
            receiptCopy.add(receipt.get(i));
        }
        return receiptCopy;
    }
}
