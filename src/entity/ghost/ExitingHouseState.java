package entity.ghost;

public class ExitingHouseState implements GhostState {
    private int stage = 0; // 0: move to return spot, 1: move to exit door, 2: move outside

    @Override
    public void update(Ghost ghost) {
        boolean movedToTarget = false;
        
        switch (stage) {
            case 0: // חדש: תנועה לנקודת הביניים ('8')
                movedToTarget = ghost.moveToTarget(ghost.RETURN_TARGET_X, ghost.RETURN_TARGET_Y, Ghost.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) stage++;
                break;
            case 1: // תנועה לדלת היציאה ('4')
                movedToTarget = ghost.moveToTarget(ghost.EXIT_TILE_X, ghost.EXIT_TILE_Y, Ghost.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) stage++;
                break;
            case 2: // תנועה אל מחוץ לבית ('0')
                movedToTarget = ghost.moveToTarget(ghost.TILE_ABOVE_EXIT_X, ghost.TILE_ABOVE_EXIT_Y, Ghost.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) {
                    // לפני המעבר למצב רדיפה, שחרר את נתיב היציאה
                    ghost.gp.setExitingLaneBusy(false); // חדש: מאותת שהרמזור ירוק
                    ghost.setState(new ChasingState()); // המעבר הסופי למצב רדיפה
                }
                break;
        }
    }
}