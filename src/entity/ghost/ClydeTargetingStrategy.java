package entity.ghost;

public class ClydeTargetingStrategy implements TargetingStrategy {
    private final int chaseRadius;

    // שימו לב, הבנאי כבר לא צריך את קואורדינטות הפיזור
    public ClydeTargetingStrategy(int chaseRadius) {
        this.chaseRadius = chaseRadius;
    }

    @Override
    public int[] getTargetTile(Ghost ghost) {
        int playerCol = ghost.gp.player.x / ghost.gp.tileSize;
        int playerRow = ghost.gp.player.y / ghost.gp.tileSize;
        int ghostCol = ghost.x / ghost.gp.tileSize;
        int ghostRow = ghost.y / ghost.gp.tileSize;

        double distance = Math.sqrt(Math.pow(ghostCol - playerCol, 2) + Math.pow(ghostRow - playerRow, 2));

        if (distance <= chaseRadius) {
            // אם השחקן קרוב, רדוף אחריו
            return new int[]{playerCol, playerRow};
        } else {
            // אם השחקן רחוק, החזר "סימן" מיוחד לנוע באקראי
            // במקום לכוון לפינה קבועה.
            return new int[]{-1, -1};
        }
    }
}