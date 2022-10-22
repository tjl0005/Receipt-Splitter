package receipt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Used to generate a checkbox list from a receipt list model
 */
public class Checklist extends JCheckBox implements ListCellRenderer<Checklist.CheckboxListItem> {

    @Override
    public Component getListCellRendererComponent(JList<? extends CheckboxListItem> list, CheckboxListItem value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        setEnabled(list.isEnabled());
        setSelected(value.isSelected());
        setText(value.toString());
        setBackground(Color.WHITE);

        return this;
    }

    // Selectable item in list
    public static class CheckboxListItem {
        private final String label;
        private boolean isSelected = false;

        /**
         * @param label the contents of the checkbox list item
         */
        public CheckboxListItem(String label) {
            this.label = label;
        }

        /**
         * Used to generate a JList of checkbox list items that can be displayed and interacted with
         * @param receiptModel the receipt model to generate a checkbox list from
         * @return a checkbox list containing each element of the receipt model
         */
        public static JList<CheckboxListItem> generateList(DefaultListModel<String> receiptModel) {
            DefaultListModel<CheckboxListItem> myList = new DefaultListModel<>();
            myList.addElement(new CheckboxListItem( String.format("%55s", "Select All")));

            for (int i=0;i < receiptModel.size();i++)
            {
                myList.addElement(new CheckboxListItem(receiptModel.get(i)));
            }

            JList<CheckboxListItem> list = new JList<>(myList);

            list.setCellRenderer(new Checklist());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            return list;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public String toString() {
            return label;
        }
    }

    /**
     * Used to set up the scroll pane containing the checkbox list and some basic formatting
     * @param displayList the checkbox list containing the contents to be displayed
     * @return the scroll pane containing the receipt to be displayed
     */
    public static JScrollPane buildScrollPane(JList<CheckboxListItem> displayList){
        // Define new scrollPane
        JScrollPane scrollPane = new JScrollPane(displayList);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        return scrollPane;
    }

    /**
     * Used to get the currently selected items from the list and update their status and to detect if select all has
     * is selected in which all items of the list will be updated
     * @param event the mouse event from a checkbox
     * @param currentSelection a list containing the indexes of the currently selected checkbox items
     */
    public static void getSelectedItems(MouseEvent event, List<Integer> currentSelection) {
        @SuppressWarnings("unchecked") // No need to check cast
        JList<Checklist.CheckboxListItem> list = (JList<Checklist.CheckboxListItem>) event.getSource();
        currentSelection.clear();
        // Get the selected item
        int listIndex = list.locationToIndex(event.getPoint());
        Checklist.CheckboxListItem item = list.getModel().getElementAt(listIndex);

        item.setSelected(!item.isSelected()); // Update selection status

        // Selecting all check boxes
        if (listIndex == 0) {
            // Update status for all checkboxes
            for (int i = 1; i < list.getModel().getSize(); i++) {
                Checklist.CheckboxListItem current = list.getModel().getElementAt(i);
                current.setSelected(item.isSelected()); // Using boolean from select all index to ensure universal switch
                if (item.isSelected()) {
                    currentSelection.add(i - 1); // -1 to make up for taken index of "Select All" option
                }
            }
        } else {
            listIndex--; // Making up for usage of "Select all function"
            // Update tracking of current selections
            if (item.isSelected()) {
                currentSelection.add(listIndex);
            } else {
                currentSelection.remove((Integer) listIndex);
            }
        }
        list.repaint();
    }
}
