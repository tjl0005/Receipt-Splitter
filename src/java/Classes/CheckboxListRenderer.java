package Classes;

import javax.swing.*;
import java.awt.*;

public class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<CheckboxListRenderer.CheckboxListItem> {

    @Override
    public Component getListCellRendererComponent(JList<? extends CheckboxListItem> list, CheckboxListItem value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        setEnabled(list.isEnabled());
        setSelected(value.isSelected());
        setText(value.toString());
        return this;
    }

    // Selectable item in list
    public static class CheckboxListItem {
        private final String label;
        private boolean isSelected = false;

        public CheckboxListItem(String label) {
            this.label = label;
        }

        public static JList<CheckboxListItem> generateList(java.util.List<String> receiptList) {
            DefaultListModel<CheckboxListItem> myList = new DefaultListModel<>();

            for (String s : receiptList) {
                myList.addElement(new CheckboxListItem(s));
            }

            JList<CheckboxListItem> list = new JList<>(myList);

            list.setCellRenderer(new CheckboxListRenderer());
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
}
