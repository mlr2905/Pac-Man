package map;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import collectibles.Pellet;
import collectibles.PowerPellet;
import view.GamePanel;

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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading object images!");
        }
    }

    public void placeObjects() {
        gp.collectables.clear();

        int[][] map = MapData.INITIAL_MAP_DATA;

        for (int row = 0; row < gp.maxScreenRow; row++) {
            for (int col = 0; col < gp.maxScreenCol; col++) {
                int tileNum = map[row][col];
                int worldX = col * gp.tileSize;
                int worldY = row * gp.tileSize;

                if (tileNum == 0) {
                    gp.collectables.add(new Pellet(gp, worldX, worldY, pelletImage));
                }
                else if (tileNum == 7) {
                    gp.collectables.add(new PowerPellet(gp, worldX, worldY, powerPelletImage));
                }
            }
        }
        System.out.println("Placed " + gp.collectables.size() + " collectable objects.");
    }
}