package entity.ghost;


public class ExitingHouseState implements BlinkyState {

    private int stage = 0; // 0: move to return spot, 1: move to exit door, 2: move outside

    @Override
    public void update(Blinky blinky) {
        boolean movedToTarget = false;
        
        switch (stage) {
            case 0: // תנועה לנקודת החזרה ('8')
                movedToTarget = blinky.moveToTarget(blinky.config.RETURN_TO_EIGHT_TARGET_X, blinky.config.RETURN_TO_EIGHT_TARGET_Y, Blinky.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) stage++;
                break;
            case 1: // תנועה לדלת היציאה ('4')
                movedToTarget = blinky.moveToTarget(blinky.config.EXIT_TILE_X, blinky.config.EXIT_TILE_Y, Blinky.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) stage++;
                break;
            case 2: // תנועה אל מחוץ לבית ('0')
                movedToTarget = blinky.moveToTarget(blinky.config.TILE_ABOVE_EXIT_X, blinky.config.TILE_ABOVE_EXIT_Y, Blinky.MovementRule.THROUGH_HOUSE_DOOR);
                if (movedToTarget) {
                    blinky.setState(new ChasingState()); // המעבר הסופי למצב רדיפה
                }
                break;
        }
    }
}