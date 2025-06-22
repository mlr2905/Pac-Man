package managers;

import collectibles.Collectable;
import main.GamePanel;

import java.awt.Graphics2D; //  <-- הוסף ייבוא
import java.awt.Color;    //  <-- הוסף ייבוא
import java.awt.Font;     //  <-- הוסף ייבוא

public class LevelManager {

    private GamePanel gp;
    public int currentLevel = 1;
    private final int maxLevel = 3;

    // Game States
    public final int playState = 1;
    public final int transitionState = 2;
    public final int gameOverState = 3; // --- חדש: הוספת מצב סיום משחק ---
    public int gameState;

    public LevelManager(GamePanel gp) {
        this.gp = gp;
        this.gameState = playState; // Start the game in play state
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void update() {
        if (gameState == playState) {
            checkLevelCompletion();
        } else if (gameState == transitionState) {
            loadNextLevel();
            gameState = playState;
        } else if (gameState == gameOverState) {
            // --- חדש: לוגיקה למצב סיום משחק ---
            // בדוק אם השחקן לחץ על Enter כדי להתחיל מחדש
            if (gp.keyH.enterPressed) {
                gp.restartGame();
                gp.keyH.enterPressed = false; // אפס את הדגל למניעת ריסטרט כפול
            }
        }
    }

    private void checkLevelCompletion() {
        for (Collectable item : gp.collectables) {
            if (!item.isCollected()) {
                return;
            }
        }
        
        if (currentLevel < maxLevel) {
            currentLevel++;
            gameState = transitionState;
            System.out.println("All items collected! Advancing to level " + currentLevel);
        } else {
            System.out.println("All levels completed! Game Over.");
            // --- שינוי: מעבר למצב סיום משחק במקום עצירת התהליך ---
            gameState = gameOverState;
        }
    }

    private void loadNextLevel() {
        gp.tileM = new tile.TileManager(gp); 
        gp.pacMan.setDefaultValues();
        System.out.println("Level " + currentLevel + " loaded.");
    }

  
    public void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.red);
        String text = "GAME OVER";
        int x = getXforCenteredText(text, g2);
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setColor(Color.white);
        text = "Press Enter to Restart";
        x = getXforCenteredText(text, g2);
        g2.drawString(text, x, y + 60);
    }

    private int getXforCenteredText(String text, Graphics2D g2) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }
  
}