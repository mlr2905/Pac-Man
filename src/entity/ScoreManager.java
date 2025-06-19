package entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import main.GamePanel;

public class ScoreManager {

    private int score;
    private GamePanel gp;

    public ScoreManager(GamePanel gp) {
        this.gp = gp;
        this.score = 0;
    }

    public void addScore(int points) {
        this.score += points;
        System.out.println("Current Score: " + score);
    }

    public int getScore() {
        return score;
    }

    // The draw method now receives the current level as an argument
    public void draw(Graphics2D g2, int currentLevel) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);

        // Draw the score
        String scoreText = "Score: " + score;
        g2.drawString(scoreText, 10, gp.tileSize);

        // Draw the level number passed as a parameter
        String levelText = "Level: " + currentLevel;
        int x = gp.screenWidth - g2.getFontMetrics().stringWidth(levelText) - 10;
        int y = gp.tileSize;
        g2.drawString(levelText, x, y);
    }
}