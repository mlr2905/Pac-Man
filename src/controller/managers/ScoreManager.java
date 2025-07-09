package controller.managers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import view.game.GamePanel;

public class ScoreManager {

    private int score;
    private GamePanel gp;

    public ScoreManager(GamePanel gp) {
        this.gp = gp;
        this.score = 0;
    }

    public void reset(){
    this.score = 0;

    }
    public void addScore(int points) {
        this.score += points;
        System.out.println("Current Score: " + score);
    }

    public int getScore() {
        return score;
    }

    public void draw(Graphics2D g2, int currentLevel) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);

        String scoreText = "Score: " + score;
        g2.drawString(scoreText, 10, 20);

        String levelText = "Level: " + currentLevel;
        int x = gp.screenWidth - g2.getFontMetrics().stringWidth(levelText) - 10;
        g2.drawString(levelText, x, 20);
    }
}