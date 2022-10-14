package Pages;

import Classes.Receipt;
import Tabs.EditingTab;
import Tabs.ExpendituresTab;
import Tabs.LabellingTab;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class MainPage {
    final JFrame frame = new JFrame();
    final JTabbedPane tabbedPane = new JTabbedPane();
    final ImageIcon img = new ImageIcon("Assets/Logo.jpg");

    public MainPage(List<String> receipt, int tabIndex) {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout());
        frame.setSize(400, 500);
        frame.setTitle("Receipt Splitter");
        frame.setIconImage(img.getImage());

        tabbedPane.add("Edit", new EditingTab(frame, receipt));
        tabbedPane.add("Label", new LabellingTab(frame, receipt));
        tabbedPane.add("Expenditure", new ExpendituresTab(frame, receipt)); // Not yet implemented, at all
        tabbedPane.addTab("Save", null);
        tabbedPane.addTab("Restart", null);

        // Detect if user wanting to save current receipt or restart
        tabbedPane.addChangeListener(event -> {
            JTabbedPane tabbedPane = (JTabbedPane) event.getSource();
            int selTabIndex = tabbedPane.getSelectedIndex();

            if (selTabIndex == 3) {
                // Ask user for new file name and save file
                String fileName = JOptionPane.showInputDialog("Save As");
                if (!Objects.equals(fileName, null)) {
                    Receipt.writeToFile(receipt, fileName);
                }
            } else if (selTabIndex == 4) {
                // Confirm user wants to reset
                int cancel = JOptionPane.showConfirmDialog(frame, "Cancel current changes?", "Cancel",
                        JOptionPane.YES_NO_OPTION);
                if (cancel == 0) {
                    frame.dispose();
                    new StartPage();
                }
            }
        });

        // Reset to original tab
        tabbedPane.setSelectedIndex(tabIndex);
        frame.add(tabbedPane);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}