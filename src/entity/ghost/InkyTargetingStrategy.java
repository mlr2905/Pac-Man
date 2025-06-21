package entity.ghost;

import entity.pacman.PacMan;
import main.GamePanel;

public class InkyTargetingStrategy implements TargetingStrategy {

    @Override
    public int[] getTargetTile(Ghost ghost) {
        GamePanel gp = ghost.gp;
        PacMan pacMan = gp.pacMan;
        
        // גישה ישירה לבלינקי דרך GamePanel, אין צורך בחיפוש!
        Ghost blinky = gp.blinky; 

        if (blinky == null) {
            // במקרה שבלינקי עוד לא נוצר, נבצע רדיפה פשוטה אחרי פקמן
            return new int[]{pacMan.x / gp.tileSize, pacMan.y / gp.tileSize};
        }

        // 1. קבלת מיקומים של פקמן ובלינקי
        int pacManCol = pacMan.x / gp.tileSize;
        int pacManRow = pacMan.y / gp.tileSize;
        int blinkyCol = blinky.x / gp.tileSize;
        int blinkyRow = blinky.y / gp.tileSize;

        // 2. חישוב הווקטור מבלינקי לפקמן
        int vecCol = pacManCol - blinkyCol;
        int vecRow = pacManRow - blinkyRow;

        // 3. הכפלת הווקטור כדי למצוא את משבצת המטרה של אינקי
        int targetCol = blinkyCol + (2 * vecCol);
        int targetRow = blinkyRow + (2 * vecRow);

        return new int[]{targetCol, targetRow};
    }
}