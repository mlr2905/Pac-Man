package entity.state;

import controller.managers.SoundManager;
import entity.ghost.Ghost;

public class WaitingState implements GhostState {
    
    private int waitTimer = 0;
    private final int WAIT_DURATION = 3 * 60; // 3 seconds at 60 FPS
    private boolean isWaitingAfterEaten = false;

    public WaitingState() {
        // Default constructor for normal waiting (score-based exit)
        this.isWaitingAfterEaten = false;
        this.waitTimer = 0;
    }
    
    public WaitingState(boolean isWaitingAfterEaten) {
        // Constructor for waiting after being eaten
        this.isWaitingAfterEaten = isWaitingAfterEaten;
        this.waitTimer = 0;
    }

    @Override
    public void update(Ghost ghost) {

        if (isWaitingAfterEaten) {
            // Count down 3 seconds before automatically exiting
            waitTimer++;
            if (waitTimer >= WAIT_DURATION) {
                System.out.println("3 seconds passed, ghost automatically exiting house"); // Debug
                ghost.gp.requestToExit(ghost);
                ghost.setHasRequestedExit(true);
                isWaitingAfterEaten = false; // Reset flag
            }
        } else {
            // Normal behavior - wait for score trigger
            if (!ghost.hasRequestedExit()) {
                int currentScore = ghost.gp.scoreM.getScore();

                if (currentScore >= ghost.exitScoreTrigger) {
                    ghost.gp.requestToExit(ghost);
                    ghost.setHasRequestedExit(true);
                }
            }
        }

        ghost.determineRandomDirectionInHouse();
        ghost.executeMovement(Ghost.MovementRule.IN_HOUSE);
    }
}