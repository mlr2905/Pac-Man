package managers;

import entity.ghost.Ghost;
import entity.state.FrightenedState;
import java.util.List;

public class PowerPelletManager {
    private int ghostEatenCount = 0;
    private final int BASE_SCORE = 200;
    
    public void activatePowerMode(List<Ghost> ghosts) {
        ghostEatenCount = 0; // Reset counter for new power pellet
        
        for (Ghost ghost : ghosts) {
            // Only frighten ghosts that are not already eaten or returning home
            if (!ghost.isEaten() && !ghost.isReturningHome()) {
                ghost.setState(new FrightenedState());
                ghost.setFrightenedMode(true);
            }
        }
    }
    
    public int getGhostEatenScore() {
        ghostEatenCount++;
        return BASE_SCORE * ghostEatenCount;
    }
    
    public void resetGhostEatenCount() {
        ghostEatenCount = 0;
    }
}