package entity.strategy;

import entity.ghost.Ghost;

public class BlinkyTargetingStrategy implements TargetingStrategy {
    @Override
    public int[] getTargetTile(Ghost ghost) {
        int pacManCol = ghost.gp.pacMan.x / ghost.gp.tileSize;
        int pacManRow = ghost.gp.pacMan.y / ghost.gp.tileSize;
        return new int[]{pacManCol, pacManRow};
    }
}