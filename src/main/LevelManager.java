package main;

import entity.Collectable;

public class LevelManager {

    private GamePanel gp;
    public int currentLevel = 1;
    private final int maxLevel = 3;

    // Game States
    public final int playState = 1;
    public final int transitionState = 2;
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
            // Player and collectables are updated directly in GamePanel's update loop
            checkLevelCompletion();
        } else if (gameState == transitionState) {
            loadNextLevel();
            gameState = playState; // Switch back to play state after loading
        }
    }

    private void checkLevelCompletion() {
        // Check if all collectable items have been collected
        for (Collectable item : gp.collectables) {
            if (!item.isCollected()) {
                return; // If any item isn't collected, stay in the current level
            }
        }
        
        // This code only runs if the loop completes (all items are collected)
        if (currentLevel < maxLevel) {
            currentLevel++;
            gameState = transitionState; // Set state to transition to the next level
            System.out.println("All items collected! Advancing to level " + currentLevel);
        } else {
            System.out.println("All levels completed! Game Over.");
            // Add game over logic here
            gp.stopGame(); // Stop the game thread
        }
    }

    private void loadNextLevel() {
        // The collectables list is cleared and reloaded inside tileM.loadMapAndFood()
gp.tileM = new tile.TileManager(gp); 
       gp.player.setDefaultValues(); // Reset player position for the new level
        System.out.println("Level " + currentLevel + " loaded.");
    }
}
