package map;

import tile.TileSet;
import view.GamePanel;
import collectibles.Pellet;
import collectibles.PowerPellet;

public class MapLoader {
   
    public static int[][] loadLayoutAndEntities(GamePanel gp, TileSet tileSet) {
        int[][] sourceMapData = MapData.INITIAL_MAP_DATA;
        int numRows = sourceMapData.length;
        int numCols = sourceMapData[0].length;
        int[][] mapLayout = new int[numRows][numCols];

        gp.collectables.clear();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int tileType = sourceMapData[row][col];
                
                mapLayout[row][col] = tileType;

                if (tileType == 0) {
                    gp.collectables.add(new Pellet(gp, col * gp.tileSize, row * gp.tileSize, tileSet.getPelletImage()));
                } else if (tileType == 7) { 
                    gp.collectables.add(new PowerPellet(gp, col * gp.tileSize, row * gp.tileSize, tileSet.getPowerPelletImage()));
                }
            }
        }
        return mapLayout;
    }
}