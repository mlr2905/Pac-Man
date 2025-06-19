package entity.ghost;

public class WaitingState implements GhostState {
    @Override
    public void update(Ghost ghost) {
        // בדוק אם הרוח עומדת בתנאי היציאה ועדיין לא ביקשה לצאת
        if (ghost.gp.scoreM.getScore() >= ghost.exitScoreTrigger && !ghost.hasRequestedExit()) {
            ghost.gp.requestToExit(ghost); // חדש: קורא למנהל התור ב-GamePanel
            ghost.setHasRequestedExit(true); // חדש: מסמן שכבר ביקשנו כדי לא להצטרף לתור שוב ושוב
        }

        // כל עוד לא קיבלנו אישור לצאת, ממשיכים לנוע בתוך הבית
        ghost.determineRandomDirectionInHouse();
        ghost.executeMovement(Ghost.MovementRule.IN_HOUSE);
    }
}