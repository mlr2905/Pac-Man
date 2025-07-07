package entity.pacman;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Color;

import entity.Entity;
import entity.ghost.Ghost;
import view.GamePanel;
import view.animations.PacManAnimationManager;
import collectibles.Collectable;
import collectibles.Pellet;
import collectibles.PowerPellet;
import controller.KeyHandler;
import controller.PacManMovementHandler;

public class PacMan extends Entity {

    GamePanel gp;

    private PacManMovementHandler movementHandler;
    private PacManAnimationManager animationManager;
    private final int defaultX, defaultY;
    public int direction = 0;
    public Rectangle solidArea;

    public PacMan(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;

        this.movementHandler = new PacManMovementHandler(this, gp, keyH);
        this.animationManager = new PacManAnimationManager(this);
        this.defaultX = x;
        this.defaultY = y;
        solidArea = new Rectangle(8, 8, gp.tileSize - 12, gp.tileSize - 12);
        setDefaultValues();
    }

    public void resetPosition() {
        this.x = defaultX;
        this.y = defaultY;
        this.direction = 0;
    }

    public void setDefaultValues() {
        x = gp.tileSize * 18;
        y = gp.tileSize * 10;
        speed = 3;
    }

    public void update() {
        boolean moved = movementHandler.update();
        animationManager.updateAnimation(moved);
        checkCollection();
        
    }

    private void checkCollection() {
        Rectangle currentSolidArea = new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);

        // Check for tile 9
        int pacManTileCol = (x + gp.tileSize / 2) / gp.tileSize;
        int pacManTileRow = (y + gp.tileSize / 2) / gp.tileSize;
        
        if (pacManTileRow >= 0 && pacManTileRow < map.MapData.INITIAL_MAP_DATA.length &&
            pacManTileCol >= 0 && pacManTileCol < map.MapData.INITIAL_MAP_DATA[0].length) {
            
            if (map.MapData.INITIAL_MAP_DATA[pacManTileRow][pacManTileCol] == 9) {
                // Collect all pellets and add score
                for (Collectable item : gp.collectables) {
                    if (!item.isCollected()) {
                        item.setCollected(true);
                    }
                }
                gp.scoreM.addScore(362 * 10); // Add score for all pellets (362 * 10 = 3620)
                System.out.println("Special tile touched! All pellets collected! Score added: " + (362 * 10));
                return;
            }
        }

        for (Collectable item : gp.collectables) {
            if (!item.isCollected() && currentSolidArea.intersects(item.getSolidArea())) {
                item.onCollected(gp.scoreM);
            }
        }
    }
    
    private void checkSpecialTile() {
        // Get PacMan's current tile position
        int pacManTileCol = (x + gp.tileSize / 2) / gp.tileSize;
        int pacManTileRow = (y + gp.tileSize / 2) / gp.tileSize;
        
        // Check if PacMan is on a tile 9
        if (pacManTileRow >= 0 && pacManTileRow < map.MapData.INITIAL_MAP_DATA.length &&
            pacManTileCol >= 0 && pacManTileCol < map.MapData.INITIAL_MAP_DATA[0].length) {
            
            int currentTile = map.MapData.INITIAL_MAP_DATA[pacManTileRow][pacManTileCol];
            
            if (currentTile == 9) {
                // PacMan touched tile 9 - collect all pellets!
                collectAllPellets();
            }
        }
    }
    
    private void collectAllPellets() {
        System.out.println("Special tile touched! Collecting all pellets!");
        
        for (Collectable item : gp.collectables) {
            if (!item.isCollected()) {
                item.setCollected(true);
                // Add score for each pellet collected this way
                if (item instanceof collectibles.Pellet) {
                    gp.scoreM.addScore(10);
                } else if (item instanceof collectibles.PowerPellet) {
                    gp.scoreM.addScore(50);
                }
            }
        }
        
        // Bonus score for completing level this way
        gp.scoreM.addScore(500);
        System.out.println("All pellets collected! Bonus: 500 points!");
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = animationManager.getFrame();

        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
            System.out.println("Warning: pacMan image is null.");
        }
    }
}