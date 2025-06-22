package tile;

import java.awt.Graphics2D;

import main.GamePanel;
import map.MapLoader;

public class TileManager {

    private static final int EMPTY_TILE_ID = 0;
    private static final int SPECIAL_SPAWN_TILE_ID = 7;

    private final GamePanel gp;
    private final TileSet tileSet;
    public final int[][] map;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        this.tileSet = new TileSet();
        this.map = MapLoader.loadLayoutAndEntities(gp, tileSet);
    }

    public void draw(Graphics2D g2) {
        final int drawTileSize = gp.tileSize;

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tileNum = map[row][col];

                if (tileNum == EMPTY_TILE_ID || tileNum == SPECIAL_SPAWN_TILE_ID) {
                    continue;
                }

                Tile tileToDraw = tileSet.getTile(tileNum);

                if (tileToDraw.getImage() != null) {
                    int x = col * drawTileSize;
                    int y = row * drawTileSize;
                    g2.drawImage(tileToDraw.getImage(), x, y, drawTileSize, drawTileSize, null);
                }
            }
        }
    }
}