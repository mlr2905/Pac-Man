package entity.ghost.state;

import entity.ghost.Ghost;

public class WaitingState implements GhostState {

    @Override
    public void update(Ghost ghost) {
        
        if (!ghost.hasRequestedExit()) {

            int currentScore = ghost.gp.scoreM.getScore();

            if (currentScore >= ghost.exitScoreTrigger) {

                ghost.gp.requestToExit(ghost);
                ghost.setHasRequestedExit(true);
            }
        }

        ghost.determineRandomDirectionInHouse();
        ghost.executeMovement(Ghost.MovementRule.IN_HOUSE);
    }
}