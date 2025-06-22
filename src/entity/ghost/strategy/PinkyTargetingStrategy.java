package entity.ghost.strategy;

import entity.ghost.Ghost;
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

        switch (pacMan.direction) {

            case 0:
                targetRow += ambushDistance;
                break;

            case 1:
                targetCol += ambushDistance;
                break;
            case 2:
                targetCol -= ambushDistance;
                break;
            case 3: 
                targetRow -= ambushDistance;
                break;
        }

        return new int[] { targetCol, targetRow };
    }
}