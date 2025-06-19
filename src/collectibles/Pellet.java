package collectibles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.Entity;
import main.GamePanel;
import main.ScoreManager;

import java.awt.Rectangle;

// Make Pellet implement the Collectable interface
public class Pellet extends Entity implements Collectable {
    GamePanel gp;
    public BufferedImage image;
    public boolean collected = false;
    public Rectangle solidArea;

    public Pellet(GamePanel gp, int x, int y, BufferedImage image) {
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.image = image;
        
        int pelletSize = gp.tileSize / 4;
        int offset = (gp.tileSize - pelletSize) / 2;
        solidArea = new Rectangle(x + offset, y + offset, pelletSize, pelletSize);
    }

    @Override
    public void update() {
        // Regular pellets don't need updates (like blinking), so this is empty.
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!collected) {
            int pelletSize = gp.tileSize / 4;
            int offset = (gp.tileSize - pelletSize) / 2;
            g2.drawImage(image, x + offset, y + offset, pelletSize, pelletSize, null);
        }
    }

    @Override
    public boolean isCollected() {
        return collected;
    }

    @Override
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public Rectangle getSolidArea() {
        return solidArea;
    }

    @Override
    public void onCollected(ScoreManager sm) {
        setCollected(true);
        sm.addScore(10); // Specific score for a regular pellet
        System.out.println("Pellet collected!");
    }
}