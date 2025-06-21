package entity.ghost;

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
            // If Pac-Man is far away, chase him directly
            return new int[]{pacManCol, pacManRow};
        } else {
            // If Pac-Man is close, wander away to his scatter target (e.g., a corner)
            // We can return a special value to signal random movement in the ChasingState.
            return new int[]{-1, -1};
        }
    }
} 