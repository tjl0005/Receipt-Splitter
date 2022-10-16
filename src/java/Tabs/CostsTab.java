package Tabs;

import Pages.MainPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class CostsTab extends JPanel {
    public CostsTab(JFrame frame, List<String> receipt, List<String> labels) {
        DefaultListModel<String> jList = new DefaultListModel<>();
        JScrollPane scrollPane = new JScrollPane(new JList<>(jList));
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setBackground(Color.WHITE);

        if (labels.isEmpty()){
            this.add(new JLabel("No labels added yet"));
        } else{
            final JComboBox<String> selectionBox = new JComboBox<>(labels.toArray(new String[0]));
            selectionBox.addActionListener(e -> {
                int selIndex = selectionBox.getSelectedIndex();
                List<String> labelledLines = getLabelledLines(labels.get(selIndex), receipt);

                if (labelledLines.isEmpty()) {
                    labels.remove(selIndex);
                    new MainPage(receipt, labels, 3);
                    frame.dispose();
                } else {
                    // Remove label prefix from labels
                    labelledLines.replaceAll(s -> s.replaceAll("<.*> ", ""));

                    jList.removeAllElements();
                    jList.addAll(labelledLines);
                }
                this.revalidate();
            });

            this.add(selectionBox);
            this.add(scrollPane);
            this.add(new JLabel("Label Breakdown"));
            this.add(setTable(labels, receipt));
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }
        this.setVisible(true);
    }

    public List<String> getLabelledLines(String label, List<String> receipt){
        List<String> labelledLines = new ArrayList<>();
        for (String line : receipt) {
            if (line.contains(label)) {
                labelledLines.add(line);
            }
        }
        return labelledLines;
    }


    public JScrollPane setTable(List<String> labels, List<String> receipt){
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Label");
        model.addColumn("No.");
        model.addColumn("Cost");

        for (String currentLabel : labels) {
            int labelCount = getLabelledLines(currentLabel, receipt).size();
            String clearLabel = currentLabel.substring(1, currentLabel.length() - 2);
            model.addRow(new Object[]{clearLabel, labelCount, "N/A"});
        }

        JTable table = new JTable(model);

        table.setRowSelectionAllowed(false);
        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setPreferredSize(new Dimension(380, 300));
        return tablePane;
    }

}


