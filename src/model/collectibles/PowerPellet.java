package collectibles;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import entity.Entity;
import managers.ScoreManager;
import managers.PowerPelletManager;
import view.GamePanel;

public class PowerPellet extends Entity implements Collectable {

    GamePanel gp;
    public BufferedImage image;
    public boolean collected = false;
    public Rectangle solidArea;

    // For blinking effect
    private boolean visible = true;
    private int blinkCounter = 0;
    private final int BLINK_RATE = 15; // Blink every 15 frames

    public PowerPellet(GamePanel gp, int x, int y, BufferedImage image) {
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.image = image;

        // Initialize solidArea for collision detection
        int pelletSize = gp.tileSize / 2; // Power pellets are a bit larger
        int offset = (gp.tileSize - pelletSize) / 2;
        solidArea = new Rectangle(x + offset, y + offset, pelletSize, pelletSize);
    }

    @Override
    public void update() {
        // Create a blinking effect
        blinkCounter++;
        if (blinkCounter > BLINK_RATE) {
            visible = !visible;
            blinkCounter = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!collected && visible) {
            int pelletSize = gp.tileSize / 2+10;
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
        sm.addScore(50); // Score for a power pellet
        
        // Activate frightened mode for all ghosts
        if (gp.powerPelletManager != null) {
            gp.powerPelletManager.activatePowerMode(gp.getAllGhosts());
        }
        
        System.out.println("Power Pellet collected! Ghosts are now frightened!");
    }
}