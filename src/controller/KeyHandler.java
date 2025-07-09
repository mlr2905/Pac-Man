package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import view.game.GamePanel;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean enterPressed;
    private GamePanel gamePanel; // הוספת הפניה ל-GamePanel
    
    public KeyHandler() {
        // קונסטרקטור ריק לתאימות לאחור
    }
    
    public KeyHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    
    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // אם נמצאים במצב הכנסת שם, טיפול בהכנסת טקסט
        if (gamePanel != null && gamePanel.isEnteringName()) {
            char keyChar = e.getKeyChar();
            // אפשר כל תו שניתן להדפסה חוץ מ-Enter ו-Backspace (הם מטופלים ב-keyPressed)
            if (Character.isLetterOrDigit(keyChar) || keyChar == ' ') {
                gamePanel.handleNameInput(keyChar);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        // אם נמצאים במצב הכנסת שם, רק Enter ו-Backspace
        if (gamePanel != null && gamePanel.isEnteringName()) {
            if (code == KeyEvent.VK_ENTER) {
                gamePanel.handleNameInput('\n');
            } else if (code == KeyEvent.VK_BACK_SPACE) {
                gamePanel.handleNameInput('\b');
            }
            return; // חשוב! מונע טיפול נוסף במקשים
        }
        
        // טיפול רגיל בקלטים רק אם לא נמצאים במצב הכנסת שם
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) enterPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_UP) upPressed = false;
        if (code == KeyEvent.VK_DOWN) downPressed = false;
        if (code == KeyEvent.VK_LEFT) leftPressed = false;
        if (code == KeyEvent.VK_RIGHT) rightPressed = false;
        if (code == KeyEvent.VK_ENTER) enterPressed = false;
    }
}