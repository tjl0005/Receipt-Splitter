package Tabs;

import javax.swing.*;
import java.util.List;

// This will allow a user to view a selection from labels

public class ExpendituresTab extends JPanel {
    public ExpendituresTab(JFrame frame, List<String> receipt) {
        System.out.println(frame);
        System.out.println(receipt);
        this.setVisible(true);
    }

}


