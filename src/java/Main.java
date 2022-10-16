import pages.StartPage;

import javax.swing.*;
import java.awt.*;

class Main {
    public static void main(String[] args) {
        setLook();
        setUIFont();
        new StartPage();
    }

    private static void setLook() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

    }

    private static void setUIFont() {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(new Font("Arial", Font.PLAIN, 20)));
            }
        }
    }
}