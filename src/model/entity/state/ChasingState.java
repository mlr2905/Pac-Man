package entity.state;

import entity.ghost.Ghost;
import entity.ghost.GhostNavigator;

public class ChasingState implements GhostState {
    @Override
    public void update(Ghost ghost) {
        boolean onGrid = (ghost.x % ghost.gp.tileSize == 0) && (ghost.y % ghost.gp.tileSize == 0);
        boolean isStuck = !ghost.canMoveInDirection(ghost.getCurrentMovingDirection(), Ghost.MovementRule.CHASE);

        int pacManTileCol = ghost.gp.pacMan.x / ghost.gp.tileSize;
        int pacManTileRow = ghost.gp.pacMan.y / ghost.gp.tileSize;
        boolean pacManMoved = (pacManTileCol != ghost.lastpacManTileX || pacManTileRow != ghost.lastpacManTileY);

        if (onGrid || isStuck || pacManMoved) {
            ghost.lastpacManTileX = pacManTileCol;
            ghost.lastpacManTileY = pacManTileRow;

            int[] target = ghost.targetingStrategy.getTargetTile(ghost);

            if (target[0] == -1) {
                ghost.determineRandomDirectionInMaze();
            } else {
                int targetCol = target[0];
                int targetRow = target[1];
                int ghostCol = ghost.x / ghost.gp.tileSize;
                int ghostRow = ghost.y / ghost.gp.tileSize;

                String pathDirection = GhostNavigator.getShortestPathDirection(ghostCol, ghostRow, targetCol,
                        targetRow);

                if (ghost.canMoveInDirection(pathDirection, Ghost.MovementRule.CHASE)) {

                    ghost.setRequestedDirection(pathDirection);
                } else {
                    ghost.determineRandomDirectionInMaze();
                }
            }
        }

        ghost.executeMovement(Ghost.MovementRule.CHASE);
    }
}