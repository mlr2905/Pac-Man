package entity.ghost;

public class WaitingState implements GhostState {

    @Override
    public void update(Ghost ghost) {
        // בדוק אם הרוח עדיין לא ביקשה לצאת
        if (!ghost.hasRequestedExit()) {
            
            // קבל את הניקוד הנוכחי מהמשחק
            int currentScore = ghost.gp.scoreM.getScore(); // ודא שיש לך מתודת getScore() ב-ScoreManager

            // --- זה התיקון המרכזי ---
            // בדוק אם הניקוד הנוכחי גדול או שווה לסף היציאה של הרוח
            if (currentScore >= ghost.exitScoreTrigger) {
                
                // אם כן, בקש לצאת מהבית והרם את הדגל
                ghost.gp.requestToExit(ghost);
                ghost.setHasRequestedExit(true);
            }
        }
        
        ghost.determineRandomDirectionInHouse();
        ghost.executeMovement(Ghost.MovementRule.IN_HOUSE);
    }
}