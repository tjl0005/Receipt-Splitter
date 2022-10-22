import com.formdev.flatlaf.FlatLightLaf;
import pages.StartPage;

import javax.swing.*;

class Main {
    public static void main(String[] args) {
        setLook();
        new StartPage();
    }

    /**
     * Update the UI manager to the use the flat laf look and feel
     */
    private static void setLook() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

    }
}