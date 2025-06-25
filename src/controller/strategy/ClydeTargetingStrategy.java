package controller.strategy;

import entity.ghost.Ghost;

public class ClydeTargetingStrategy implements TargetingStrategy {
    private final int chaseRadius;

    public ClydeTargetingStrategy(int chaseRadius) {
        this.chaseRadius = chaseRadius;
    }

    @Override
    public int[] getTargetTile(Ghost ghost) {
        int pacManCol = ghost.gp.pacMan.x / ghost.gp.tileSize;
        int pacManRow = ghost.gp.pacMan.y / ghost.gp.tileSize;
        int ghostCol = ghost.x / ghost.gp.tileSize;
        int ghostRow = ghost.y / ghost.gp.tileSize;

        double distance = Math.sqrt(Math.pow(ghostCol - pacManCol, 2) + Math.pow(ghostRow - pacManRow, 2));

        if (distance > chaseRadius) {
            return new int[]{pacManCol, pacManRow};
        } else {
              return new int[]{-1, -1};
        }
    }
} 