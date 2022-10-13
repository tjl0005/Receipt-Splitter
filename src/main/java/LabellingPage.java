import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LabellingPage implements ActionListener {
    private final List<String> currentReceipt;
    List<Integer> currentSelection = new ArrayList<>(); // Store selected indexes of selected lines
    JTextField labelTextField = new JTextField("Enter new label");

    final JFrame frame = new JFrame();
    final JPanel buttonPanel = new JPanel();
    final JPanel receiptPanel = new JPanel();

    final JButton viewButton = new JButton("View Receipt");
    final JButton labelButton = new JButton("Add Label");
    final JButton resetButton = new JButton("Restart");

    public LabellingPage(List<String> originalReceipt) {
        currentReceipt = originalReceipt;
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setTitle("Receipt Splitter - Label");
        frame.setLayout(new FlowLayout());

        // Create check box list with current receipt
        JList<CheckboxListRenderer.CheckboxListItem> list = CheckboxListRenderer.CheckboxListItem.generateList(currentReceipt);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                JList<CheckboxListRenderer.CheckboxListItem> list = (JList<CheckboxListRenderer.CheckboxListItem>) event.getSource();

                // Get the selected line
                int listIndex = list.locationToIndex(event.getPoint());
                CheckboxListRenderer.CheckboxListItem item = list.getModel().getElementAt(listIndex);

                // Update to reflect either selection or deselection
                item.setSelected(!item.isSelected());
                list.repaint(list.getCellBounds(listIndex, listIndex));

                // Update tracking of current selections
                if (item.isSelected()) {
                    currentSelection.add(listIndex);
                } else {
                    currentSelection.remove((Integer) listIndex);
                }

                // Only enabled if a selection is made
                labelButton.setEnabled(currentSelection.size() > 0);
            }
        });

        labelButton.setBounds(50, 80, 100, 25);
        labelButton.setFocusPainted(false);
        labelButton.addActionListener(this);
        // Only enabled if a selection is made
        labelButton.setEnabled(currentSelection.size() > 0);


        viewButton.setBounds(225, 200, 100, 25);
        viewButton.setFocusPainted(false);
        viewButton.addActionListener(this);

        resetButton.setBounds(225, 250, 100, 25);
        resetButton.setFocusPainted(false); // Remove highlight box
        resetButton.addActionListener(this);

        buttonPanel.add(labelButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(resetButton);

        receiptPanel.add(new JScrollPane(list));

        frame.add(receiptPanel);
        frame.add(buttonPanel);
        frame.add(labelTextField);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == labelButton){
            // Add label to selected lines by updating their value
            for (int index : currentSelection){
                currentReceipt.set(index, labelTextField.getText() + " " + currentReceipt.get(index));
            }

            frame.dispose();
            new LabellingPage(currentReceipt);
        }

        // Redirect to viewing page
        if (e.getSource() == viewButton){
            frame.dispose();
            new ViewingPage(currentReceipt);
        }

        if (e.getSource() == resetButton){
            System.out.println("Reset button pressed");
            frame.dispose();
            new StartPage();
        }
    }
}
