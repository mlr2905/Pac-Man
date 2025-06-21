package entity.player;

import main.KeyHandler;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import collectibles.Collectable;
import java.awt.Color;
import main.Entity;
import main.GamePanel;
import java.awt.Rectangle;

public class Player extends Entity {

    GamePanel gp;

    private PlayerMovementHandler movementHandler;
    private PlayerAnimationManager animationManager;

    public int direction = 0; 
    public Rectangle solidArea;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;

        this.movementHandler = new PlayerMovementHandler(this, gp, keyH);
        this.animationManager = new PlayerAnimationManager(this);

        solidArea = new Rectangle(8, 8, gp.tileSize - 12, gp.tileSize - 12);
        setDefaultValues();
    }

    public void setDefaultValues() {
        x = (gp.screenWidth / 2) - 18 - (gp.tileSize / 2);
        y = (gp.screenHeight / 2) + 30 - (gp.tileSize / 2);
        speed = 3;

    }

    public void update() {
        boolean moved = movementHandler.update();
        animationManager.updateAnimation(moved);
        checkCollection();
    }

    private void checkCollection() {
        Rectangle currentSolidArea = new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);

        for (Collectable item : gp.collectables) {
            if (!item.isCollected() && currentSolidArea.intersects(item.getSolidArea())) {
                item.onCollected(gp.scoreM);
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = animationManager.getFrame();

        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
            System.out.println("Warning: Player image is null.");
        }
    }
}