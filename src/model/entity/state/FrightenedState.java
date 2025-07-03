package entity.state;

import entity.ghost.Ghost;

public class FrightenedState implements GhostState {
    private int frightenedTimer;
    private final int FRIGHTENED_DURATION = 30 * 60; // 30 seconds at 60 FPS
    private final int BLINK_WARNING_TIME = 5 * 60; // 5 seconds before end
    private boolean isBlinking = false;
    private int blinkCounter = 0;
    private final int BLINK_RATE = 15; // Blink every 15 frames

    public FrightenedState() {
        this.frightenedTimer = FRIGHTENED_DURATION;
    }

    @Override
    public void update(Ghost ghost) {
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