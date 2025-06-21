package entity.ghost;

public class ChasingState implements GhostState {
    // בקובץ ChasingState.java
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
                // מקרה קלייד: תנועה אקראית
                ghost.determineRandomDirectionInMaze();
            } else {
                // מקרה מרדף רגיל
                int targetCol = target[0];
                int targetRow = target[1];
                int ghostCol = ghost.x / ghost.gp.tileSize;
                int ghostRow = ghost.y / ghost.gp.tileSize;

                String pathDirection = GhostNavigator.getShortestPathDirection(ghostCol, ghostRow, targetCol,
                        targetRow);

                // --- חדש: בדיקה קריטית למניעת היתקעות ---
                // האם הכיוון מה-AI פנוי מהמיקום הנוכחי המדויק?
                if (ghost.canMoveInDirection(pathDirection, Ghost.MovementRule.CHASE)) {
                    // אם כן, קבע אותו ככיוון המבוקש
                    ghost.setRequestedDirection(pathDirection);
                } else {
                    // אם לא (נתקענו בפינה), חפש כל כיוון פנוי אחר כדי להיחלץ.
                    // המתודה הקיימת לתנועה אקראית מושלמת למשימה זו.
                    ghost.determineRandomDirectionInMaze();
                }
            }
        }

        ghost.executeMovement(Ghost.MovementRule.CHASE);
    }
}