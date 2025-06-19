package entity.ghost;

// אסטרטגיית המרדף הפשוטה של בלינקי: תמיד רודף אחרי השחקן.
public class BlinkyTargetingStrategy implements TargetingStrategy {
    @Override
    public int[] getTargetTile(Ghost ghost) {
        int playerCol = ghost.gp.player.x / ghost.gp.tileSize;
        int playerRow = ghost.gp.player.y / ghost.gp.tileSize;
        return new int[]{playerCol, playerRow};
    }
}