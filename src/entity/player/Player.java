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
    
    // Handlers for specific logic
    private PlayerMovementHandler movementHandler;
    private PlayerAnimationManager animationManager;

    // Public state that handlers might need to access/modify
    public int direction = 0; // 0=down, 1=right, 2=left, 3=up
    public Rectangle solidArea; 

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        
        // Initialize the handlers
        this.movementHandler = new PlayerMovementHandler(this, gp, keyH);
        this.animationManager = new PlayerAnimationManager(this);

        solidArea = new Rectangle(8, 8, gp.tileSize - 12, gp.tileSize - 12);
        setDefaultValues();
    }

    public void setDefaultValues() {
    x = (gp.screenWidth / 2) -18- (gp.tileSize / 2);

    // חישוב נקודת האמצע האנכית של המסך, והתאמה לגודל השחקן
    y = (gp.screenHeight / 2) +30- (gp.tileSize / 2);
        speed = 3;
   
    }
    
    /**
     * The Player's update method now delegates tasks to its handlers.
     */
    public void update() {
        boolean moved = movementHandler.update(); // Update movement and get status
        animationManager.updateAnimation(moved);   // Update animation based on movement
        checkCollection();                         // Handle interactions
    }

    /**
     * Checks for collision with any collectable item.
     * This logic remains in Player as it's about interaction, not just movement.
     */
    private void checkCollection() {
        Rectangle currentSolidArea = new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);

        for (Collectable item : gp.collectables) {
            if (!item.isCollected() && currentSolidArea.intersects(item.getSolidArea())) {
                item.onCollected(gp.scoreM);
            }
        }
    }
    
    /**
     * The Player's draw method gets the current frame from the animation manager.
     */
    public void draw(Graphics2D g2) {
        BufferedImage image = animationManager.getFrame();

        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        } else {
            // Fallback drawing if image fails to load
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
            System.out.println("Warning: Player image is null.");
        }
    }
}