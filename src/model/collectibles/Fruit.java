package collectibles;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import managers.ScoreManager;
import view.GamePanel;

public class Fruit implements Collectable {
    
    public enum FruitType {
        CHERRY(100, "/view/resources/fruits/cherry.png"),
        STRAWBERRY(300, "/view/resources/fruits/strawberry.png"),
        ORANGE(500, "/view/resources/fruits/orange.png"),
        APPLE(700, "/view/resources/fruits/apple.png"),
        MELON(1000, "/view/resources/fruits/melon.png");
        
        private final int points;
        private final String imagePath;
        
        FruitType(int points, String imagePath) {
            this.points = points;
            this.imagePath = imagePath;
        }
        
        public int getPoints() {
            return points;
        }
        
        public String getImagePath() {
            return imagePath;
        }
    }
    
    private GamePanel gp;
    private int x, y;
    private FruitType fruitType;
    private BufferedImage image;
    private long spawnTime;
    private long duration; // משך זמן הצגה במילישניות
    private boolean isActive;
    private boolean collected;
    private Rectangle solidArea;
    
    public Fruit(GamePanel gp, int x, int y, FruitType fruitType, long duration) {
        this.gp = gp;
        this.x = x;
        this.y = y;
        this.fruitType = fruitType;
        this.duration = duration;
        this.spawnTime = System.currentTimeMillis();
        this.isActive = true;
        this.collected = false;
        this.solidArea = new Rectangle(4, 4, gp.tileSize - 8, gp.tileSize - 8);
        loadImage();
    }
    
    private void loadImage() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream(fruitType.getImagePath()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading fruit image: " + fruitType.getImagePath());
        }
    }
    
    @Override
    public void update() {
        // בדיקה אם הפרי פג תוקף
        if (isActive && System.currentTimeMillis() - spawnTime > duration) {
            isActive = false;
            setCollected(true); // מסמן כנאסף כדי שיוסר מהמסך
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
    public void onCollected(ScoreManager scoreManager) {
        if (!isCollected() && isActive) {
            setCollected(true);
            scoreManager.addScore(fruitType.getPoints());
            System.out.println("Fruit collected! Type: " + fruitType.name() + 
                             ", Points: " + fruitType.getPoints());
        }
    }
    
    @Override
    public void draw(Graphics2D g2) {
        if (!isCollected() && isActive && image != null) {
            // גודל 20 פיקסל במקום gp.tileSize
            int fruitSize = 20;
            
            // מרכוז הפרי בתוך ה-tile
            int offsetX = (gp.tileSize - fruitSize) / 2;
            int offsetY = (gp.tileSize - fruitSize) / 2;
            
            g2.drawImage(image, x + offsetX, y + offsetY, fruitSize, fruitSize, null);
        }
    }
    
    @Override
    public Rectangle getSolidArea() {
        // מרכוז ה-solidArea בתוך ה-tile
        int fruitSize = 20;
        int offsetX = (gp.tileSize - fruitSize) / 2;
        int offsetY = (gp.tileSize - fruitSize) / 2;
        
        return new Rectangle(x + offsetX + solidArea.x, y + offsetY + solidArea.y, 
                           solidArea.width, solidArea.height);
    }
    
    public boolean isActive() {
        return isActive && !isCollected();
    }
    
    public FruitType getFruitType() {
        return fruitType;
    }
    
    public long getRemainingTime() {
        if (!isActive) return 0;
        long elapsed = System.currentTimeMillis() - spawnTime;
        return Math.max(0, duration - elapsed);
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}