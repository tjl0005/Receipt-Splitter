import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewingPage implements ActionListener {
    private final List<String> currentReceipt;

    final JFrame frame = new JFrame();
    final JPanel buttonPanel = new JPanel();
    final JPanel receiptPanel = new JPanel();

    final JButton editButton = new JButton("Edit");
    final JButton labelButton = new JButton("Label");
    final JButton saveButton = new JButton("Save");
    final JButton resetButton = new JButton("Restart");

    JTextField labelTextField = new JTextField("File name");

    public ViewingPage(List<String> originalReceipt) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setTitle("Receipt Splitter - View");
        frame.setLayout(new FlowLayout());

        // Make receipt accessible
        currentReceipt = originalReceipt;

        editButton.setBounds(125, 420, 100, 25);
        editButton.addActionListener(this);
        labelButton.setBounds(225, 420, 100, 25);
        labelButton.addActionListener(this);
        resetButton.setBounds(325, 420, 100, 25);
        resetButton.addActionListener(this);

        // Display the receipt as scrollable
        JList<String> displayReceipt = getDisplayList(currentReceipt);
        JScrollPane scrollPane = new JScrollPane(displayReceipt);
        receiptPanel.add(scrollPane);

        buttonPanel.add(editButton);
        buttonPanel.add(labelButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(labelTextField);
        buttonPanel.setFocusable(false);

        frame.add(receiptPanel);
        frame.add(buttonPanel);
        frame.setVisible(true);
    }

    public JList<String> getDisplayList(List<String> receipt){
        // Convert receipt into JList
        DefaultListModel<String> jList = new DefaultListModel<>();
        jList.addAll(receipt);

        return new JList<>(jList);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Redirect to editing page
        if (e.getSource() == editButton){
            frame.dispose();
            new EditingPage(currentReceipt);
        }

        // Redirect to labelling page
        if (e.getSource() == labelButton){
            System.out.println("Label button pressed");
            frame.dispose();
            new LabellingPage(currentReceipt);
        }

        // Save receipt in current state
        if (e.getSource() == saveButton){
            System.out.println("Save button pressed");
            // TODO: Open directory saved to -> Implement into receipt class?
            receipt.writeToFile(currentReceipt, labelTextField.getText());
        }

        // Return to start
        if (e.getSource() == resetButton){
            System.out.println("Reset button pressed");
            frame.dispose();
            new StartPage();
        }
    }
}
