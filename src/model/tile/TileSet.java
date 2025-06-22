package tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class TileSet {

    private final Tile[] tiles;
    private final BufferedImage pelletImage;
    private final BufferedImage powerPelletImage;
    private final Tile defaultTile;

    public TileSet() {
        try {
            BufferedImage emptyImage = loadImage("/view/resources/tiles/000.png");
            BufferedImage wallImage = loadImage("/view/resources/tiles/wall.png");
            BufferedImage doorImage = loadImage("/view/resources/tiles/door.png");

            this.pelletImage = loadImage("/view/resources/objects/pellet.png");
            this.powerPelletImage = loadImage("/view/resources/objects/powerPellet.png");

            this.defaultTile = new Tile(emptyImage, false);

            this.tiles = new Tile[9];
            tiles[0] = defaultTile;
            tiles[1] = new Tile(wallImage, true);
            tiles[2] = defaultTile;
            tiles[3] = defaultTile;
            tiles[4] = new Tile(doorImage, true);
            tiles[5] = defaultTile;
            tiles[6] = defaultTile;
            tiles[7] = defaultTile;
            tiles[8] = defaultTile;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("שגיאה קריטית בטעינת תמונות האריחים. המשחק לא יכול להתחיל.", e);
        }
    }

    private BufferedImage loadImage(String path) throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            Objects.requireNonNull(stream, "לא ניתן למצוא את המשאב: " + path);
            return ImageIO.read(stream);
        }
    }

    public Tile getTile(int index) {
        if (index >= 0 && index < tiles.length && tiles[index] != null) {
            return tiles[index];
        }
        return defaultTile;
    }

    public BufferedImage getPelletImage() {
        return pelletImage;
    }

    public BufferedImage getPowerPelletImage() {
        return powerPelletImage;
    }
}