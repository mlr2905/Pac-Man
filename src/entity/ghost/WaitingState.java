package entity.ghost;

public class WaitingState implements GhostState {
    @Override
    public void update(Ghost ghost) {
        if (ghost.gp.scoreM.getScore() >= ghost.exitScoreTrigger && !ghost.hasRequestedExit()) {
            ghost.gp.requestToExit(ghost); 
            ghost.setHasRequestedExit(true);
        }

        ghost.determineRandomDirectionInHouse();
        ghost.executeMovement(Ghost.MovementRule.IN_HOUSE);
    }
}