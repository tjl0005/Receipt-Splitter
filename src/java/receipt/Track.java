package receipt;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides undo/redo functionality to the application
 */
public class Track {
    /**
     * Store the changes that occur and can be undone
     */
    public static List<List<String>> undoTracker = new ArrayList<>();
    /**
     * Store undone changes that can be redone
     */
    public static List<List<String>> redoTracker = new ArrayList<>();

    /**
     * If a change is made to be made to a receipt a copy of it will be stored prior to editing, so it can be reverted,
     * upto 5 changes will be saved at a time.
     * @param receipt the list model containing the receipt contents to be recorded prior to being edited
     */
    public static void recordChange(DefaultListModel<String> receipt){
        // Only tracking upto 5 changes
        if (undoTracker.size() > 5) {
            undoTracker.remove(0);
        }
        undoTracker.add(getContents(receipt));
    }

    /**
     * Undo the last change made to a receipt whilst updating the redo tracker
     * @param receipt the receipt model to be updated
     */
    public static void undo(DefaultListModel<String> receipt){
        redoTracker.add(getContents(receipt));
        receipt.clear();
        receipt.addAll(undoTracker.get(undoTracker.size() - 1)); // Get the newest change and update model with it
        undoTracker.remove(undoTracker.size() - 1); // Remove this change from the tracker
    }

    /**
     * Redo an undone action whilst updating the undo tracker
     * @param receipt the receipt model to be updated
     */
    public static void redo(DefaultListModel<String> receipt){
        undoTracker.add(getContents(receipt));
        receipt.clear();
        receipt.addAll(redoTracker.get(redoTracker.size() - 1)); // Get the newest change and update model with it
        redoTracker.remove(redoTracker.size() - 1); // Remove this change from the tracker
    }

    /**
     * Prepare the contents of a receipt model as a list for easier recording of contents
     * @param receipt the receipt model to get the contents as a list of strings
     * @return a list of strings containing the receipts elements
     */
    public static List<String> getContents(DefaultListModel<String> receipt){
        List<String> receiptCopy = new ArrayList<>();
        for (int i = 0; i < receipt.size(); i++){
            receiptCopy.add(receipt.get(i));
        }
        return receiptCopy;
    }
}
