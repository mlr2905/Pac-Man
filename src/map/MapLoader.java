package map;

import main.GamePanel;
import tile.TileSet;
import collectibles.Pellet;
import collectibles.PowerPellet;

public class MapLoader {

    /**
     * טוען את מבנה המפה מ-MapData ומאכלס את רשימת המתכלים ב-GamePanel.
     * @param gp ה-GamePanel הראשי של המשחק.
     * @param tileSet ערכת האריחים והתמונות שכבר נטענה.
     * @return מערך דו-מימדי (int[][]) המייצג את מפת האריחים.
     */
    public static int[][] loadLayoutAndEntities(GamePanel gp, TileSet tileSet) {
        int[][] sourceMapData = MapData.INITIAL_MAP_DATA;
        int numRows = sourceMapData.length;
        int numCols = sourceMapData[0].length;
        int[][] mapLayout = new int[numRows][numCols];

        // נקה את רשימת המתכלים לפני טעינת שלב חדש
        gp.collectables.clear();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int tileType = sourceMapData[row][col];
                
                // העתק את סוג האריח למערך שיוחזר
                mapLayout[row][col] = tileType;

                // בדוק אם יש צורך ליצור אובייקט מתכלה במיקום זה
                // 0 מסמן מיקום של Pellet
                if (tileType == 0) {
                    gp.collectables.add(new Pellet(gp, col * gp.tileSize, row * gp.tileSize, tileSet.getPelletImage()));
                // 7 מסמן מיקום של PowerPellet
                } else if (tileType == 7) { 
                    gp.collectables.add(new PowerPellet(gp, col * gp.tileSize, row * gp.tileSize, tileSet.getPowerPelletImage()));
                }
            }
        }
        return mapLayout;
    }
}