package entity.pacman;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.Color;

import entity.Entity;
import entity.ghost.Ghost;
import view.animations.PacManAnimationManager;
import view.game.GamePanel;
import collectibles.Collectable;
import collectibles.Pellet;
import collectibles.PowerPellet;
import collectibles.Fruit; // הוספה חדשה
import controller.KeyHandler;
import controller.PacManMovementHandler;

public class PacMan extends Entity {

    GamePanel gp;

    private PacManMovementHandler movementHandler;
    private PacManAnimationManager animationManager;
    private final int defaultX, defaultY;
    public int direction = 0;
    public Rectangle solidArea;
    private boolean specialTileTriggered = false; // דגל למניעת הפעלה חוזרת

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
        this.specialTileTriggered = false; // איפוס הדגל
    }

    public void setDefaultValues() {
        x = gp.tileSize * 18;
        y = gp.tileSize * 10;
        speed = 3;
        specialTileTriggered = false; // איפוס הדגל
    }

    public void update() {
        boolean moved = movementHandler.update();
        animationManager.updateAnimation(moved);
        checkCollection();
        
    }

    private void checkCollection() {
        Rectangle currentSolidArea = new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);

        // Check for tile 9 - רק אם עוד לא הופעל
        if (!specialTileTriggered) {
            int pacManTileCol = (x + gp.tileSize / 2) / gp.tileSize;
            int pacManTileRow = (y + gp.tileSize / 2) / gp.tileSize;
            
            if (pacManTileRow >= 0 && pacManTileRow < map.MapData.INITIAL_MAP_DATA.length &&
                pacManTileCol >= 0 && pacManTileCol < map.MapData.INITIAL_MAP_DATA[0].length) {
                
                if (map.MapData.INITIAL_MAP_DATA[pacManTileRow][pacManTileCol] == 9) {
                    // הפעלת האפקט המיוחד
                    specialTileTriggered = true;
                    collectAllPellets();
                    return;
                }
            }
        }

        // בדיקת איסוף רגיל של פריטים
        for (Collectable item : gp.collectables) {
            if (!item.isCollected() && currentSolidArea.intersects(item.getSolidArea())) {
                // בדיקה מיוחדת לפירות - הדפסת הודעה מפורטת יותר
                if (item instanceof Fruit) {
                    Fruit fruit = (Fruit) item;
                    System.out.println("Fruit collected! Type: " + fruit.getFruitType().name() + 
                                     ", Points: " + fruit.getFruitType().getPoints());
                }
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
        
        int pelletsCollected = 0;
        int powerPelletsCollected = 0;
        
        for (Collectable item : gp.collectables) {
            if (!item.isCollected()) {
                item.setCollected(true);
                // ספירת סוגי הפלטות
                if (item instanceof collectibles.Pellet) {
                    pelletsCollected++;
                    gp.scoreM.addScore(10);
                } else if (item instanceof collectibles.PowerPellet) {
                    powerPelletsCollected++;
                    gp.scoreM.addScore(50);
                }
            }
        }
        
        // בונוס נקודות לסיום הרמה בדרך זו
        int bonusScore = 500;
        gp.scoreM.addScore(bonusScore);
        
        System.out.println("All pellets collected! Pellets: " + pelletsCollected + 
                         ", Power Pellets: " + powerPelletsCollected + 
                         ", Bonus: " + bonusScore + " points!");
        
        // בדיקה אם כל הפלטות נאספו - סיום הרמה
        checkLevelComplete();
    }
    
    private void checkLevelComplete() {
        boolean allPelletsCollected = true;
        
        for (Collectable item : gp.collectables) {
            if ((item instanceof collectibles.Pellet || item instanceof collectibles.PowerPellet) 
                && !item.isCollected()) {
                allPelletsCollected = false;
                break;
            }
        }
        
        if (allPelletsCollected) {
            System.out.println("Level Complete!");
            // כאן תוכל להוסיף לוגיקה למעבר לרמה הבאה
            // לדוגמה:
            // gp.levelManager.nextLevel();
        }
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