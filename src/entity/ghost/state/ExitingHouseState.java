package entity.ghost.state;

import entity.ghost.Ghost;

public class ExitingHouseState implements GhostState {
    private int stage = 0;

    @Override
    public void update(Ghost ghost) {
        boolean movedToTarget = false;
        
        switch (stage) {
            case 0: 
                movedToTarget = ghost.moveToTarget(ghost.RETURN_TARGET_X, ghost.RETURN_TARGET_Y, Ghost.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) stage++;
                break;
            case 1: 
                movedToTarget = ghost.moveToTarget(ghost.EXIT_TILE_X, ghost.EXIT_TILE_Y, Ghost.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) stage++;
                break;
            case 2: 
                movedToTarget = ghost.moveToTarget(ghost.TILE_ABOVE_EXIT_X, ghost.TILE_ABOVE_EXIT_Y, Ghost.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) {
                    ghost.gp.setExitingLaneBusy(false); 
                    ghost.setState(new ChasingState()); 
                }
                break;
        }
    }
}