package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


public class UIManager {
    
    private GamePanel gp;

    public UIManager(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        if (gp.levelManager.gameState != gp.levelManager.transitionState) {
            gp.tileM.draw(g2);
            gp.entityManager.draw(g2);
            gp.scoreM.draw(g2, gp.levelManager.getCurrentLevel());
            drawLives(g2);
        }

        if (gp.levelManager.gameState == gp.levelManager.gameOverState) {
            drawGameOverScreen(g2);
        }
    }

    private void drawLives(Graphics2D g2) {
        if (gp.lifeImage == null) return;
        for (int i = 0; i < gp.lives; i++) {
            g2.drawImage(gp.lifeImage, gp.tileSize * (i + 1), gp.screenHeight - gp.tileSize - 10, gp.tileSize, gp.tileSize, null);
        }
    }

    private void drawGameOverScreen(Graphics2D g2) {
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