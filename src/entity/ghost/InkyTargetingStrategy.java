package entity.ghost;

import entity.pacman.PacMan;
import main.GamePanel;

public class InkyTargetingStrategy implements TargetingStrategy {

    @Override
    public int[] getTargetTile(Ghost ghost) {
        GamePanel gp = ghost.gp;
        PacMan pacMan = gp.pacMan;
        
        Ghost blinky = gp.blinky; 
    
        int pacManCol = pacMan.x / gp.tileSize;
        int pacManRow = pacMan.y / gp.tileSize;
        int blinkyCol = blinky.x / gp.tileSize;
        int blinkyRow = blinky.y / gp.tileSize;

        int vecCol = pacManCol - blinkyCol;
        int vecRow = pacManRow - blinkyRow;

        int targetCol = blinkyCol + (2 * vecCol);
        int targetRow = blinkyRow + (2 * vecRow);
        return new int[]{targetCol, targetRow};
    }
}