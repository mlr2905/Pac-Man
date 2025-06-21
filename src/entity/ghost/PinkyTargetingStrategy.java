package entity.ghost;

import entity.pacman.PacMan;

public class PinkyTargetingStrategy implements TargetingStrategy {

    private final int ambushDistance;

    public PinkyTargetingStrategy(int ambushDistance) {
        this.ambushDistance = ambushDistance;
    }

    @Override
    public int[] getTargetTile(Ghost ghost) {
        PacMan pacMan = ghost.gp.pacMan;
        int targetCol = pacMan.x / ghost.gp.tileSize;
        int targetRow = pacMan.y / ghost.gp.tileSize;

        // --- קוד מתוקן ---
        // המשתנה pacMan.direction הוא int, לכן נשתמש במספרים ב-switch.
        // אנו מניחים את המיפוי הבא, בהתבסס על הקוד של Ghost.java:
        // 0 = down, 1 = right, 2 = left, 3 = up
        // אם המיפוי במחלקת PacMan שלך שונה, שנה את המספרים ב-case בהתאם.
        switch (pacMan.direction) {
            case 3: // "up"
                targetRow -= ambushDistance;
                break;
            case 0: // "down"
                targetRow += ambushDistance;
                break;
            case 2: // "left"
                targetCol -= ambushDistance;
                break;
            case 1: // "right"
                targetCol += ambushDistance;
                break;
        }

        return new int[]{targetCol, targetRow};
    }
}