package controller.managers;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import HighScore.HighScore;

public class HighScoreManager {
    private static final String HIGHSCORE_FILE = "highscores.txt";
    private static final int MAX_HIGHSCORES = 10;
    private List<HighScore> highScores;
    
    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadHighScores();
    }
    
    public void addHighScore(String playerName, int score) {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        HighScore newScore = new HighScore(playerName, score, date);
        
        highScores.add(newScore);
        highScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        if (highScores.size() > MAX_HIGHSCORES) {
            highScores.remove(highScores.size() - 1);
        }
        
        saveHighScores();
    }
    
    public List<HighScore> getHighScores() {
        return new ArrayList<>(highScores);
    }
    
    public boolean isHighScore(int score) {
        return highScores.size() < MAX_HIGHSCORES || 
               score > highScores.get(highScores.size() - 1).getScore();
    }
    
    private void loadHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    highScores.add(new HighScore(parts[0], Integer.parseInt(parts[1]), parts[2]));
                }
            }
            highScores.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        } catch (IOException e) {
            System.out.println("No previous high scores found or error reading file.");
        }
    }
    
    private void saveHighScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGHSCORE_FILE))) {
            for (HighScore score : highScores) {
                writer.println(score.getPlayerName() + "," + score.getScore() + "," + score.getDate());
            }
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }
}