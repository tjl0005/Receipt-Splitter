package pages;

import classes.Receipt;
import tabs.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainPage {
    final JFrame frame = new JFrame();
    final JTabbedPane tabbedPane = new JTabbedPane();
    final ImageIcon img = new ImageIcon("Assets/Logo.jpg");
    final JLabel userID = new JLabel("User");

    public MainPage(List<String> receipt, List<String> labels) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());
        frame.setSize(400, 590);
        frame.setTitle("Receipt Splitter");
        frame.setIconImage(img.getImage());
        frame.setResizable(false);

        // Contains the receipt in its current state
        DefaultListModel receiptModel = new DefaultListModel();
        receiptModel.setValue(receipt);
        // Contains current labels
        DefaultListModel labelModel = new DefaultListModel();
        labelModel.setValue(labels);

        // Declare tabs outside tabbedPane so they can be updated
        ReceiptsTab receiptsTab = new ReceiptsTab(userID.getText(), receiptModel);
        EditingTab editTab = new EditingTab(receiptModel);
        LabellingTab labellingTab =  new LabellingTab(labelModel, receiptModel);
        CostsTab costsTab = new CostsTab(labelModel, receiptModel);

        tabbedPane.add("All Receipts", receiptsTab);
        tabbedPane.add("Edit", editTab);
        tabbedPane.add("Label", labellingTab);
        tabbedPane.add("Costs", costsTab);

        // Not available until labels exist
        tabbedPane.setEnabledAt(3, false);

        tabbedPane.getModel().addChangeListener(e -> {
        // Update scroll panes
        editTab.setScrollPane(receiptModel.getValue());
        labellingTab.setScrollPane(receiptModel.getValue());

        // Labels need to be present
        if (!labelModel.getValue().isEmpty()){
            tabbedPane.setEnabledAt(3, true); // User can now access costs tab
            // Update combo box and table due to label updates
            costsTab.setComboBox(labelModel, receiptModel);
            costsTab.setTable(labelModel, receiptModel);
        }
        });

        frame.add(tabbedPane);
        frame.setJMenuBar(createMenu(receiptModel.getValue(), labelModel.getValue()));
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    private JMenuBar createMenu(List<String> receipt, List<String> labels) {
        JMenuBar menu = new JMenuBar();

        JButton refreshButton = new JButton("Refresh");
        JButton saveButton = new JButton("Save");
        JButton resetButton = new JButton("New Receipt");

        refreshButton.setFocusable(false);
        saveButton.setFocusable(false);
        resetButton.setFocusable(false);

        // Open new version of this frame
        refreshButton.addActionListener(e -> {
            frame.dispose();
            new MainPage(receipt, labels);
        });

        // Save receipt with given name
        saveButton.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog("Save As");
            if (!Objects.equals(fileName, null)) {
                Receipt.writeToFile(receipt, userID.getText(), fileName);
                frame.dispose();
                new MainPage(receipt, labels);
            }
        });

        // Restart the application
        resetButton.addActionListener(e -> {
            int cancel = JOptionPane.showConfirmDialog(frame, "If current receipt is not saved it will be lost."
                            + "", "Continue?", JOptionPane.YES_NO_OPTION);
            if (cancel == 0) {
                frame.dispose();
                new StartPage();
            }
        });

        menu.add(refreshButton);
        menu.add(saveButton);
        menu.add(resetButton);
        menu.add(new JSeparator());
        menu.add(userID);
        menu.add(new JSeparator());

        return menu;
    }

    public interface ListModel {
        interface Observer {
            void valueDidChange(ListModel source, List<String> value);
        }
        List<String> getValue();
        @SuppressWarnings("unused") // It is used
        void addObserver(ListModel.Observer observer);
    }

    public interface MutableListModel extends ListModel {
        void setValue(List<String> value);
    }

    public static class DefaultListModel implements MutableListModel {

        private List<String> value;

        private final List<Observer> observers;

        public DefaultListModel() {
            this(new ArrayList<>());
        }

        public DefaultListModel(List<String> value) {
            this.value = value;
            observers = new ArrayList<>();
        }

        @Override
        public void setValue(List<String> value) {
            this.value = value;
            fireValueDidChange(value);
        }

        @Override
        public List<String> getValue() {
            return value;
        }

        @Override
        public void addObserver(Observer observer) {
            observers.add(observer);
        }

        protected void fireValueDidChange(List<String> value) {
            for (Observer observer : observers) {
                observer.valueDidChange(this, value);
            }
        }
    }
}