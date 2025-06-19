package tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class TileSet {

    private final Tile[] tiles;
    private BufferedImage pelletImage;
    private BufferedImage powerPelletImage;

    public TileSet() {
        tiles = new Tile[9]; // הגדרת גודל המערך, כעת עד 8
        loadImages();
    }

    private void loadImages() {
        try {
            tiles[0] = new Tile(); // רצפה ריקה
            tiles[0].image = loadImage("/resources/tiles/000.png");

            tiles[1] = new Tile(); // קיר
            tiles[1].image = loadImage("/resources/tiles/wall.png");
            tiles[1].collision = true;
            tiles[2] = new Tile();
            tiles[2].image = tiles[0].image; 
            // אריחים אחרים שמוצגים כרצפה (או סוגים ספציפיים)
            tiles[3] = new Tile(); // טלפורט / רצפה
            tiles[3].image = loadImage("/resources/tiles/000.png");

            tiles[4] = new Tile(); // דלת / פתח יציאה
            tiles[4].image = loadImage("/resources/tiles/door.png"); // אולי כדאי לתת לו קוליז'ן כשדלת סגורה
            tiles[4].collision = true; // אם זו דלת שנסגרת / נפתחת

            tiles[5] = new Tile(); // לרוב משמש ל-Power Pellet, אך כאן יכול להיות רצפה
            tiles[5].image = loadImage("/resources/tiles/000.png");

            tiles[6] = new Tile(); // אריח בית רוח
            tiles[6].image = loadImage("/resources/tiles/000.png");

            tiles[7] = new Tile(); // אריח בית רוח / נקודת חזרה
            tiles[7].image = loadImage("/resources/tiles/000.png");

            // הוספת אריח 8
            tiles[8] = new Tile(); // אריח התחלה של Blinky
            tiles[8].image = loadImage("/resources/tiles/000.png"); // תמונה לאריח 8
            
            pelletImage = loadImage("/resources/tiles/pellet.png");
            powerPelletImage = loadImage("/resources/tiles/powerPellet.png");

            // השתמש בתמונת הרצפה הריקה

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("שגיאה קריטית בטעינת תמונות האריחים.");
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            throw new IOException("לא ניתן למצוא את המשאב: " + path);
        }
        return ImageIO.read(stream);
    }

    public Tile getTile(int index) {
        if (index >= 0 && index < tiles.length && tiles[index] != null) {
            return tiles[index];
        }
        return tiles[0];
    }

    public BufferedImage getPelletImage() {
        return pelletImage;
    }

    public BufferedImage getPowerPelletImage() {
        return powerPelletImage;
    }
}