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
            // Only frighten ghosts that are outside the house (not in WaitingState or ExitingHouseState)
            if (!ghost.isEaten() && !ghost.isReturningHome() && 
                !ghost.isInWaitingState() && !ghost.isExitingHouse()) {
                ghost.setState(new FrightenedState());
                ghost.setFrightenedMode(true);
                System.out.println("Ghost frightened!");
            } else {
                System.out.println("Ghost NOT frightened (in house, eaten, or returning home)");
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