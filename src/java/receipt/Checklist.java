package receipt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

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

        public CheckboxListItem(String label) {
            this.label = label;
        }

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

    public static JScrollPane buildScrollPane(JList<CheckboxListItem> list){
        // Define new scrollPane
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        return scrollPane;
    }

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
                current.setSelected(!current.isSelected());
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
