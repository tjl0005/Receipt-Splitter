import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class EditingPage implements ActionListener {
    private final List<String> currentReceipt;
    List<Integer> currentSelection = new ArrayList<>();

    final JFrame frame = new JFrame();
    final JPanel buttonPanel = new JPanel();
    final JPanel receiptPanel = new JPanel();

    JTextField editTextField = new JTextField("Select a line to change.");

    final JButton editButton = new JButton("Edit");
    final JButton addButton = new JButton("Add");
    final JButton deleteButton = new JButton("Delete");
    final JButton viewButton = new JButton("View Receipt");
    final JButton resetButton = new JButton("Restart");

    public EditingPage(List<String> originalReceipt) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setTitle("Receipt Splitter - Edit");
        frame.setLayout(new FlowLayout());

        currentReceipt = originalReceipt;
        buttonsValid();

        addButton.setBounds(30, 80, 100, 25);
        addButton.setFocusPainted(false);
        addButton.addActionListener(this);

        editButton.setBounds(130, 80, 100, 25);
        editButton.setFocusPainted(false);
        editButton.addActionListener(this);

        deleteButton.setBounds(230, 80, 100, 25);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(this);

        JList<CheckboxListRenderer.CheckboxListItem> list = CheckboxListRenderer.CheckboxListItem.generateList(currentReceipt);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                JList<CheckboxListRenderer.CheckboxListItem> list = (JList<CheckboxListRenderer.CheckboxListItem>) event.getSource();

                // Get the selected item
                int listIndex = list.locationToIndex(event.getPoint());
                CheckboxListRenderer.CheckboxListItem item = list.getModel().getElementAt(listIndex);

                item.setSelected(!item.isSelected());
                list.repaint(list.getCellBounds(listIndex, listIndex));

                // Update tracking of current selections
                if (item.isSelected()) {
                    currentSelection.add(listIndex);
                } else {
                    currentSelection.remove((Integer) listIndex);
                }

                buttonsValid();
            }
        });

        receiptPanel.add(new JScrollPane(list));

        viewButton.setBounds(225, 350, 100, 25);
        viewButton.setFocusPainted(false);
        viewButton.addActionListener(this);

        resetButton.setBounds(335, 350, 100, 25);
        resetButton.setFocusPainted(false); // Remove highlight box
        resetButton.addActionListener(this);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(resetButton);

        frame.add(receiptPanel);
        frame.add(buttonPanel);
        frame.add(editTextField);
        frame.setVisible(true);
    }


    // Hide buttons until valid selection made
    public void buttonsValid(){
        editButton.setEnabled(currentSelection.size() == 1);
        addButton.setEnabled(currentSelection.size() == 1);
        deleteButton.setEnabled(currentSelection.size() > 0);

        if (currentSelection.size() == 1){
            editTextField.setText(currentReceipt.get(currentSelection.get(0)));
        }
        else{
            editTextField.setText("Only one line may be edited at a time");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton){
            currentReceipt.add(currentSelection.get(0), editTextField.getText());
            frame.dispose();
            new EditingPage(currentReceipt);
        }

        if (e.getSource() == editButton){
            editButton.setEnabled(true);
            int editIndex = currentSelection.get(0);

            currentReceipt.set(editIndex, editTextField.getText());
            frame.dispose();
            new EditingPage(currentReceipt);
         }

        if (e.getSource() == deleteButton){
            for (int index : currentSelection){
                currentReceipt.remove(index);
            }
            frame.dispose();
            new EditingPage(currentReceipt);
        }

        if (e.getSource() == viewButton){
            System.out.println("Confirmation button pressed pressed");

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
