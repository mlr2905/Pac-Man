package entity.ghost; // ודא שכל קבצי הרוח תחת אותה חבילה

public class ChasingState implements BlinkyState {

@Override

public void update(Blinky blinky) {
    // --- שלב 0: בדיקת "תפיסה" - האם הגענו לשחקן? ---
    // נבדוק אם הרוח קרובה מספיק לשחקן כדי להחשיב את המרדף כמוצלח.
    int distanceX = Math.abs(blinky.x - blinky.gp.player.x);
    int distanceY = Math.abs(blinky.y - blinky.gp.player.y);

    // אם המרחק קטן (למשל, פחות מחצי אריח), הרוח נעצרת.
    // זה פותר את בעיית הדיוק של פיקסלים בודדים.
    if (distanceX < blinky.gp.tileSize / 2 && distanceY < blinky.gp.tileSize / 2) {
        blinky.setRequestedDirection("none"); // פקודה לעצור
        blinky.executeMovement(Blinky.MovementRule.CHASE);
        return; // סיים את העדכון הנוכחי, אין צורך לחשב נתיב.
    }

    // --- אם לא הגענו, המשך בלוגיקת המרדף הרגילה ---
    boolean onGrid = (blinky.x % blinky.gp.tileSize == 0) && (blinky.y % blinky.gp.tileSize == 0);
    boolean isStuck = !blinky.canMoveInDirection(blinky.getCurrentMovingDirection(), Blinky.MovementRule.CHASE);

    int playerTileCol = blinky.gp.player.x / blinky.gp.tileSize;
    int playerTileRow = blinky.gp.player.y / blinky.gp.tileSize;
    boolean playerMoved = (playerTileCol != blinky.lastPlayerTileX || playerTileRow != blinky.lastPlayerTileY);

    if (onGrid || isStuck || playerMoved) {
        blinky.lastPlayerTileX = playerTileCol;
        blinky.lastPlayerTileY = playerTileRow;
        
        int blinkyTileCol = blinky.x / blinky.gp.tileSize;
        int blinkyTileRow = blinky.y / blinky.gp.tileSize;
        
        String pathDirection = GhostNavigator.getShortestPathDirection(blinkyTileCol, blinkyTileRow, playerTileCol, playerTileRow);

        if (!"none".equals(pathDirection)) {
            blinky.setRequestedDirection(pathDirection);
        } else {
            // כאן, 'none' באמת אומר שאין נתיב, כי כבר בדקנו אם הגענו.
            // לכן, אפשר לעבור ישירות לשיטות הגיבוי.
            String greedyDirection = blinky.getDirectionToTile(playerTileCol, playerTileRow);
            if (blinky.canMoveInDirection(greedyDirection, Blinky.MovementRule.CHASE)) {
                blinky.setRequestedDirection(greedyDirection);
            } else {
                blinky.determineRandomDirectionInMaze();
            }
        }
    }

    blinky.executeMovement(Blinky.MovementRule.CHASE);
}
}