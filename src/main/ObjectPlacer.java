package main;

import entity.Pellet;
import entity.PowerPellet;
import map.MapData;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ObjectPlacer {

    private GamePanel gp;
    private BufferedImage pelletImage;
    private BufferedImage powerPelletImage;

    public ObjectPlacer(GamePanel gp) {
        this.gp = gp;
        loadObjectImages();
    }

    private void loadObjectImages() {
        try {
            pelletImage = ImageIO.read(getClass().getResourceAsStream("/resources/objects/pellet.png"));
            powerPelletImage = ImageIO.read(getClass().getResourceAsStream("/resources/objects/power_pellet.png"));
            // ודא שהתמונות האלה קיימות בתיקיית resources/objects
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading object images!");
        }
    }

    public void placeObjects() {
        // Clear existing collectables before placing new ones for a new level
        gp.collectables.clear();

        int[][] map = MapData.INITIAL_MAP_DATA; // In a real game, you might load different maps per level

        for (int row = 0; row < gp.maxScreenRow; row++) {
            for (int col = 0; col < gp.maxScreenCol; col++) {
                int tileNum = map[row][col];
                int worldX = col * gp.tileSize;
                int worldY = row * gp.tileSize;

                // Tile '0' is a regular pellet
                if (tileNum == 0) {
                    gp.collectables.add(new Pellet(gp, worldX, worldY, pelletImage));
                }
                // Tile '7' is a power pellet
                else if (tileNum == 7) {
                    gp.collectables.add(new PowerPellet(gp, worldX, worldY, powerPelletImage));
                }
            }
        }
        System.out.println("Placed " + gp.collectables.size() + " collectable objects.");
    }
}