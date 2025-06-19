package tile;

import main.GamePanel;
import map.MapLoader;

import java.awt.Graphics2D;

public class TileManager {

    private final GamePanel gp;
    private final TileSet tileSet; // מחזיק את כל תמונות האריחים
    public final int[][] map;   // מבנה המפה הסופי (מכיל רק מספרים)

    public TileManager(GamePanel gp) {
        this.gp = gp;
        this.tileSet = new TileSet();
        this.map = MapLoader.loadLayoutAndEntities(gp, tileSet);
    }

   
    public void draw(Graphics2D g2) {
        int drawTileSize = gp.tileSize;

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tileNum = map[row][col];
                
              
                if (tileNum != 0 && tileNum != 7) {
                    Tile tileToDraw = tileSet.getTile(tileNum);
                    if (tileToDraw.image != null) {
                        int x = col * drawTileSize;
                        int y = row * drawTileSize;
                        g2.drawImage(tileToDraw.image, x, y, drawTileSize, drawTileSize, null);
                    }
                }
            }
        }
    }
}