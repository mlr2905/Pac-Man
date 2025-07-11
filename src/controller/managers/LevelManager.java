package controller.managers;

import java.awt.Graphics2D; 
import java.awt.Color;    
import java.awt.Font;

import collectibles.Collectable;
import entity.state.FrightenedState;
import view.game.GamePanel;

public class LevelManager {

    private GamePanel gp;
    public int currentLevel = 1;
    private final int maxLevel = 3;

    public final int playState = 1;
    public final int transitionState = 2;
    public final int gameOverState = 3;
    public int gameState;

    public LevelManager(GamePanel gp) {
        this.gp = gp;
        this.gameState = playState; 
    }

    public LevelManager(FrightenedState frightenedState) {
        //TODO Auto-generated constructor stub
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
        
            if (gp.keyH.enterPressed) {
                gp.restartGame();
                gp.keyH.enterPressed = false; 
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
            gp.lives++;
            

            gameState = transitionState;
            System.out.println("All items collected! Advancing to level " + currentLevel);
        } else {
            System.out.println("All levels completed! Game Over.");
            gameState = gameOverState;
        }
    }

    private void loadNextLevel() {
        gp.tileM = new view.tile.TileManager(gp); 
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