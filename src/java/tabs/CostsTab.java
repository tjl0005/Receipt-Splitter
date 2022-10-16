package tabs;

import pages.MainPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class CostsTab extends JPanel {
    JComboBox<String> selectionBox = new JComboBox<>();
    final JPanel displayPanel = new JPanel();
    final JPanel tablePanel = new JPanel();
    final DefaultListModel<String> jList = new DefaultListModel<>();
    final JScrollPane scrollPane = new JScrollPane(new JList<>(jList));
    JScrollPane tablePane = new JScrollPane();

    public CostsTab(MainPage.DefaultListModel labelModel, MainPage.DefaultListModel receiptModel) {
        scrollPane.setPreferredSize(new Dimension(380, 200));
        tablePanel.setPreferredSize(new Dimension(380, 300));
        scrollPane.setBackground(Color.WHITE);

        setComboBox(labelModel, receiptModel);
        setTable(labelModel, receiptModel);

        displayPanel.add(selectionBox);
        displayPanel.add(scrollPane);

        this.add(displayPanel);
        this.add(new JLabel("Label Breakdown"));
        this.add(tablePanel);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.setVisible(true);
    }

    public void setComboBox(MainPage.DefaultListModel labelModel, MainPage.DefaultListModel receiptModel){
        // Remove old versions
        displayPanel.remove(selectionBox);
        displayPanel.remove(scrollPane);

        // Setup combobox
        selectionBox = new JComboBox<>(labelModel.getValue().toArray(new String[0]));
        selectionBox.addActionListener(e -> {
            List<String> labels = labelModel.getValue();
            int selIndex = selectionBox.getSelectedIndex();
            List<String> labelledLines = getLabelledLines(labelModel.getValue().get(selIndex), receiptModel.getValue());

            if (labelledLines.isEmpty()) {
                labels.remove(selIndex);
                labelModel.setValue(labels);
            } else {
                jList.removeAllElements();
                jList.addAll(labelledLines);
            }
            this.revalidate();
        });

        // Display first label contents by default
        List<String> currentLabels = labelModel.getValue();
        if (!currentLabels.isEmpty()){
            List<String> defaultLines = getLabelledLines(labelModel.getValue().get(0), receiptModel.getValue());
            jList.removeAllElements();
            jList.addAll(defaultLines);
        }

        // Refresh with updated display panel
        displayPanel.add(selectionBox);
        displayPanel.add(scrollPane);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // Get all lines that have the given label
    public List<String> getLabelledLines(String label, List<String> receipt){
        List<String> labelledLines = new ArrayList<>();
        for (String line : receipt) {
            if (line.contains(label)) {
                labelledLines.add(line);
            }
        }
        labelledLines.replaceAll(s -> s.replaceAll("<.*> ", ""));  // Remove label prefix from lines
        return labelledLines;
    }


    public void setTable(MainPage.DefaultListModel labelModel, MainPage.DefaultListModel receiptModel){
        // TODO: Figure out cost thing
        // TODO: Get cell update for total cost
        tablePanel.remove(tablePane);
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("Label"); // Label reference
        model.addColumn("No."); // Number of lines labelled with this reference
        model.addColumn("Cost"); // Total cost of this label

        for (String currentLabel : labelModel.getValue()) {
            int labelCount = getLabelledLines(currentLabel, receiptModel.getValue()).size();
            String clearLabel = currentLabel.substring(1, currentLabel.length() - 2); // Removing < >
            model.addRow(new Object[]{clearLabel, labelCount, "N/A"});
        }

        JTable table = new JTable(model);

        tablePane = new JScrollPane(table);
        tablePanel.add(tablePane);
    }

}


