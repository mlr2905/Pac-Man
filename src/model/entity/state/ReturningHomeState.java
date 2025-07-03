package entity.state;

import entity.ghost.Ghost;
import entity.ghost.GhostNavigator;

public class ReturningHomeState implements GhostState {
    private final int HOME_ENTRANCE_X = 18; // Tile 4 position (שער הבית)
    private final int HOME_ENTRANCE_Y = 6;
    private final int HOME_INTERIOR_X = 18; // Tile 8 position (פנים הבית)
    private final int HOME_INTERIOR_Y = 7;
    private boolean reachedEntrance = false;

    @Override
    public void update(Ghost ghost) {
        if (!reachedEntrance) {
            // Move to home entrance (tile 4)
            int ghostCol = ghost.x / ghost.gp.tileSize;
            int ghostRow = ghost.y / ghost.gp.tileSize;
            
            System.out.println("Ghost returning home - current: (" + ghostCol + "," + ghostRow + "), target entrance: (" + HOME_ENTRANCE_X + "," + HOME_ENTRANCE_Y + ")"); // Debug
            
            if (ghostCol == HOME_ENTRANCE_X && ghostRow == HOME_ENTRANCE_Y) {
                reachedEntrance = true;
                System.out.println("Ghost reached entrance, moving to interior at (" + HOME_INTERIOR_X + "," + HOME_INTERIOR_Y + ")"); // Debug
            } else {
                // Use pathfinding to reach home entrance - use special version for eaten ghosts
                String pathDirection = GhostNavigator.getShortestPathDirectionForEaten(
                    ghostCol, ghostRow, HOME_ENTRANCE_X, HOME_ENTRANCE_Y);
                
                System.out.println("Path direction to home entrance (eaten ghost): " + pathDirection); // Debug
                
                if (ghost.canMoveInDirection(pathDirection, Ghost.MovementRule.CHASE)) {
                    ghost.setRequestedDirection(pathDirection);
                } else {
                    System.out.println("Cannot move in direction " + pathDirection + ", trying random direction"); // Debug
                    ghost.determineRandomDirectionInMaze();
                }
                
                ghost.executeMovement(Ghost.MovementRule.CHASE);
            }
        } else {
            // Move to home interior (tile 8 at position 18,7)
            System.out.println("Moving to home interior at (" + HOME_INTERIOR_X + "," + HOME_INTERIOR_Y + ")"); // Debug
            boolean reachedHome = ghost.moveToTarget(HOME_INTERIOR_X, HOME_INTERIOR_Y, Ghost.MovementRule.THROUGH_HOUSE_DOOR);
            
            if (reachedHome) {
                System.out.println("Ghost reached home interior! Entering waiting state with 3-second timer"); // Debug
                // Reset ghost state and enter waiting state with timer
                ghost.setState(new WaitingState(true)); // true = waiting after being eaten
                ghost.setFrightenedMode(false);
                ghost.setEaten(false);
                ghost.setHasRequestedExit(false); // Reset exit request
            }
        }
    }
}