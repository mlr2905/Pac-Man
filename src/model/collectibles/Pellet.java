package collectibles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import controller.managers.ScoreManager;
import controller.managers.SoundManager;
import entity.Entity;
import view.game.GamePanel;

import java.awt.Rectangle;

// Make Pellet implement the Collectable interface
public class Pellet extends Entity implements Collectable {
    GamePanel gp;
    public BufferedImage image;
    public boolean collected = false;
    public boolean temporarilyHidden = false; // הוספה חדשה - הסתרה זמנית
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
        // מציג רק אם לא נאסף ולא מוסתר זמנית
        if (!collected && !temporarilyHidden) {
            int pelletSize = gp.tileSize /2+10;
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
    // ניתן לאסוף רק אם לא מוסתר זמנית
    if (!temporarilyHidden) {
        setCollected(true);
        sm.addScore(10);
        
        // בדיקה אם עברו 5 שניות מהצליל האחרון
        long currentTime = System.currentTimeMillis();
        if (currentTime - gp.getLastPelletSoundTime() >= gp.getPelletSoundCooldown()) {
            SoundManager.getInstance().playSound("/view/resources/sounds/pellet.wav");
            gp.setLastPelletSoundTime(currentTime);
        }
        
        System.out.println("Pellet collected!");
    }
}
    
    // מתודות חדשות להסתרה זמנית
    public void setTemporarilyHidden(boolean hidden) {
        this.temporarilyHidden = hidden;
    }
    
    public boolean isTemporarilyHidden() {
        return temporarilyHidden;
    }
    
    // מתודות לקבלת מיקום (לזיהוי במנהל הפירות)
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}