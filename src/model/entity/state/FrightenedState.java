package entity.state;

import controller.managers.LevelManager;
import entity.ghost.Ghost;
import view.game.GamePanel;

public class FrightenedState implements GhostState {
    public int frightenedTimer;
    public int FRIGHTENED_DURATION;
    private final int BLINK_WARNING_TIME = 5 * 60; // 5 seconds before end
    private boolean isBlinking = false;
    private int blinkCounter = 0;
    private final int BLINK_RATE = 15; // Blink every 15 frames
    private boolean initialized = false; // Add this flag
    
    public FrightenedState() {
        // DO NOT calculate duration here
        // DO NOT access any game objects here
        // Just initialize basic values
        this.isBlinking = false;
        this.blinkCounter = 0;
        this.initialized = false;
    }
    
    private int calculateFrightenedDuration(int currentLevel) {
        switch (currentLevel) {
            case 1:
                return 25 * 60; 
            case 2:
                return 17 * 60; 
            case 3:
                return 10 * 60; 
            default:
                return 25 * 60; 
        }
    }

    @Override
    public void update(Ghost ghost) {
        // Initialize on first use when we have access to ghost.gp
        if (!initialized) {
            this.FRIGHTENED_DURATION = calculateFrightenedDuration(ghost.gp.levelManager.currentLevel);
            this.frightenedTimer = FRIGHTENED_DURATION;
            this.initialized = true;
        }
        
        frightenedTimer--;
        
        // Check if we should start blinking (5 seconds before end)
        if (frightenedTimer <= BLINK_WARNING_TIME) {
            isBlinking = true;
            blinkCounter++;
            if (blinkCounter >= BLINK_RATE) {
                ghost.switchFrightenedFrame();
                blinkCounter = 0;
            }
        }
        
        // Check if frightened time is over
        if (frightenedTimer <= 0) {
            ghost.setState(new ChasingState());
            ghost.setFrightenedMode(false);
            return;
        }
        
        // Move randomly
        boolean onGrid = (ghost.x % ghost.gp.tileSize == 0) && (ghost.y % ghost.gp.tileSize == 0);
        boolean isStuck = !ghost.canMoveInDirection(ghost.getCurrentMovingDirection(), Ghost.MovementRule.CHASE);
        
        if (onGrid || isStuck) {
            ghost.determineRandomDirectionInMaze();
        }
        
        ghost.executeMovement(Ghost.MovementRule.CHASE);
    }
    
    public boolean isBlinking() {
        return isBlinking;
    }
}