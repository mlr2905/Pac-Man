package main;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("PAC-MAN Game");

        // התחלה עם תפריט במקום ישירות עם המשחק
        MenuPanel menuPanel = new MenuPanel(window);
        window.add(menuPanel);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}