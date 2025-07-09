package main;
// MenuPanel.java

import javax.swing.*;

import HighScore.HighScore;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import controller.managers.HighScoreManager;
import view.game.GamePanel;

public class MenuPanel extends JPanel {
    private JFrame parentFrame;
    private HighScoreManager highScoreManager;
    private Font titleFont;
    private Font buttonFont;
    
    public MenuPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.highScoreManager = new HighScoreManager();
        this.titleFont = new Font("Arial", Font.BOLD, 48);
        this.buttonFont = new Font("Arial", Font.PLAIN, 24);
        
        setupPanel();
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        
        // קביעת גודל התפריט
        setPreferredSize(new Dimension(1140, 570)); // גודל זהה ל-GamePanel
        // או לחלופין גודל יותר גדול:
        // setPreferredSize(new Dimension(1400, 800));
        
        // כותרת
        JLabel titleLabel = new JLabel("PAC-MAN", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // כפתורים
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 100, 100));
        
        JButton startButton = createMenuButton("Start Game");
        JButton highScoreButton = createMenuButton("High Scores");
        JButton exitButton = createMenuButton("Exit");
        
        startButton.addActionListener(e -> startGame());
        highScoreButton.addActionListener(e -> showHighScores());
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(highScoreButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(exitButton);
        
        add(buttonPanel, BorderLayout.CENTER);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setPreferredSize(new Dimension(300, 60));
        button.setMaximumSize(new Dimension(300, 60));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // אפקטי hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.CYAN);
                button.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.BLUE);
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private void startGame() {
        parentFrame.getContentPane().removeAll();
        GamePanel gamePanel = new GamePanel();
        gamePanel.setHighScoreManager(highScoreManager);
        parentFrame.add(gamePanel);
        parentFrame.revalidate();
        parentFrame.repaint();
        gamePanel.requestFocus();
        gamePanel.startGameThread();
    }
    
    private void showHighScores() {
        JDialog dialog = new JDialog(parentFrame, "High Scores", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.getContentPane().setBackground(Color.BLACK);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);
        
        JLabel titleLabel = new JLabel("HIGH SCORES", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        List<HighScore> scores = highScoreManager.getHighScores();
        JPanel scoresPanel = new JPanel();
        scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
        scoresPanel.setBackground(Color.BLACK);
        
        if (scores.isEmpty()) {
            JLabel noScoresLabel = new JLabel("No high scores yet!", SwingConstants.CENTER);
            noScoresLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            noScoresLabel.setForeground(Color.WHITE);
            scoresPanel.add(noScoresLabel);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                HighScore score = scores.get(i);
                JLabel scoreLabel = new JLabel((i + 1) + ". " + score.toString(), SwingConstants.CENTER);
                scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                scoresPanel.add(scoreLabel);
                scoresPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(scoresPanel);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().setBackground(Color.BLACK);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        backButton.setBackground(Color.BLUE);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }
}