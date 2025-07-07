package managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import collectibles.Fruit;
import map.MapData;
import view.GamePanel;

public class FruitManager {
    
    private GamePanel gp;
    private Random random;
    private List<int[]> fruitSpawnPositions;
    private Fruit currentFruit;
    private long lastMoveTime;
    private Fruit.FruitType currentFruitType;
    private Fruit.FruitType[] fruitSequence = {
        Fruit.FruitType.CHERRY,
        Fruit.FruitType.STRAWBERRY,
        Fruit.FruitType.ORANGE,
        Fruit.FruitType.APPLE,
        Fruit.FruitType.MELON
    };
    private int currentFruitIndex;
    private boolean fruitActive;
    
    public FruitManager(GamePanel gp) {
        this.gp = gp;
        this.random = new Random();
        this.fruitSpawnPositions = new ArrayList<>();
        this.currentFruitIndex = 0; // מתחיל עם דובדבן
        this.currentFruitType = fruitSequence[currentFruitIndex];
        this.lastMoveTime = 0;
        this.fruitActive = false;
        findFruitSpawnPositions();
    }
    
    private void findFruitSpawnPositions() {
        fruitSpawnPositions.clear();
        // מחפש את כל המיקומים עם אינדקס 0 במפה (מקומות הפלטות) אבל לא 7
        for (int row = 0; row < MapData.INITIAL_MAP_DATA.length; row++) {
            for (int col = 0; col < MapData.INITIAL_MAP_DATA[0].length; col++) {
                if (MapData.INITIAL_MAP_DATA[row][col] == 0) {
                    // בדיקה שאין Power Pellet (7) בסביבה הקרובה
                    if (!isPowerPelletNearby(row, col)) {
                        fruitSpawnPositions.add(new int[]{col, row});
                    }
                }
            }
        }
        System.out.println("Found " + fruitSpawnPositions.size() + " fruit spawn positions (excluding Power Pellet areas)");
    }
    
    private boolean isPowerPelletNearby(int row, int col) {
        // בדיקה במשבצת הנוכחית ובסביבה הקרובה (רדיוס 1)
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < MapData.INITIAL_MAP_DATA.length && 
                    c >= 0 && c < MapData.INITIAL_MAP_DATA[0].length) {
                    if (MapData.INITIAL_MAP_DATA[r][c] == 7) {
                        return true; // יש Power Pellet בסביבה
                    }
                }
            }
        }
        return false;
    }
    
    public void update() {
        // אם אין פרי פעיל, יצור דובדבן בהתחלה
        if (!fruitActive) {
            spawnCurrentFruit();
            fruitActive = true;
            lastMoveTime = System.currentTimeMillis();
            return;
        }
        
        // עדכון הפרי הנוכחי
        if (currentFruit != null) {
            currentFruit.update();
            
            // בדיקה אם הפרי נאסף
            if (currentFruit.isCollected()) {
                // שחזור הפלטת במיקום שהפרי היה בו
                int fruitTileX = currentFruit.getX() / gp.tileSize;
                int fruitTileY = currentFruit.getY() / gp.tileSize;
                showPelletAtPosition(fruitTileX, fruitTileY);
                
                removeFruitFromCollectables();
                currentFruit = null;
                advanceToNextFruit();
                spawnCurrentFruit();
                lastMoveTime = System.currentTimeMillis();
                return;
            }
        }
        
        // בדיקה אם צריך להזיז את הפרי למקום חדש
        if (shouldMoveFruit()) {
            moveFruitToNewLocation();
        }
    }
    
    private boolean shouldMoveFruit() {
        if (currentFruit == null || !fruitActive) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long timeSinceLastMove = currentTime - lastMoveTime;
        long moveInterval = getMoveIntervalForLevel();
        
        return timeSinceLastMove >= moveInterval;
    }
    
    private void moveFruitToNewLocation() {
        if (fruitSpawnPositions.isEmpty() || currentFruit == null) {
            return;
        }
        
        // שחזור הפלטת במיקום הקודם
        int oldTileX = currentFruit.getX() / gp.tileSize;
        int oldTileY = currentFruit.getY() / gp.tileSize;
        showPelletAtPosition(oldTileX, oldTileY);
        
        // הסרת הפרי הנוכחי
        removeFruitFromCollectables();
        
        // יצירת פרי חדש באותו סוג במקום חדש
        spawnCurrentFruit();
        
        lastMoveTime = System.currentTimeMillis();
        System.out.println("Moved " + currentFruitType.name() + " to new location");
    }
    
    private void spawnCurrentFruit() {
        if (fruitSpawnPositions.isEmpty()) {
            System.out.println("No fruit spawn positions available!");
            return;
        }
        
        // בחירת מיקום אקראי מתוך מיקומי הפלטות
        int[] position = fruitSpawnPositions.get(random.nextInt(fruitSpawnPositions.size()));
        int worldX = position[0] * gp.tileSize;
        int worldY = position[1] * gp.tileSize;
        
        // יצירת הפרי עם זמן ארוך כדי שלא יעלם מעצמו
        long longDuration = Long.MAX_VALUE; // לא יעלם מעצמו
        
        currentFruit = new Fruit(gp, worldX, worldY, currentFruitType, longDuration);
        
        // הסתרת הפלטת באותו מיקום (שמירת המיקום הקודם)
        hidePelletAtPosition(position[0], position[1]);
        
        gp.collectables.add(currentFruit);
        
        System.out.println("Spawned " + currentFruitType.name() + " fruit at position (" + 
                         position[0] + ", " + position[1] + ")");
    }
    
    private void hidePelletAtPosition(int col, int row) {
        // מחפש פלטת באותו מיקום ומסתיר אותה
        for (collectibles.Collectable item : gp.collectables) {
            if (item instanceof collectibles.Pellet) {
                collectibles.Pellet pellet = (collectibles.Pellet) item;
                
                // בדיקה אם הפלטת באותו מיקום (בערך)
                int pelletTileX = pellet.getX() / gp.tileSize;
                int pelletTileY = pellet.getY() / gp.tileSize;
                
                if (pelletTileX == col && pelletTileY == row && !pellet.isCollected()) {
                    pellet.setTemporarilyHidden(true);
                    System.out.println("Hidden pellet at (" + col + ", " + row + ")");
                    break;
                }
            }
        }
    }
    
    private void showPelletAtPosition(int col, int row) {
        // מחפש פלטת באותו מיקום ומציג אותה שוב
        for (collectibles.Collectable item : gp.collectables) {
            if (item instanceof collectibles.Pellet) {
                collectibles.Pellet pellet = (collectibles.Pellet) item;
                
                // בדיקה אם הפלטת באותו מיקום (בערך)
                int pelletTileX = pellet.getX() / gp.tileSize;
                int pelletTileY = pellet.getY() / gp.tileSize;
                
                if (pelletTileX == col && pelletTileY == row) {
                    pellet.setTemporarilyHidden(false);
                    System.out.println("Showed pellet at (" + col + ", " + row + ")");
                    break;
                }
            }
        }
    }
    
    private void advanceToNextFruit() {
        currentFruitIndex = (currentFruitIndex + 1) % fruitSequence.length;
        currentFruitType = fruitSequence[currentFruitIndex];
        System.out.println("Next fruit will be: " + currentFruitType.name());
    }
    
    private long getMoveIntervalForLevel() {
        int currentLevel = gp.levelManager.getCurrentLevel();
        
        switch (currentLevel) {
            case 1:
                return 30000; // 30 שניות
            case 2:
                return 20000; // 20 שניות
            case 3:
            default:
                return 10000; // 10 שניות
        }
    }
    
    private void removeFruitFromCollectables() {
        if (currentFruit != null) {
            gp.collectables.remove(currentFruit);
        }
    }
    
    public void reset() {
        if (currentFruit != null) {
            removeFruitFromCollectables();
            currentFruit = null;
        }
        currentFruitIndex = 0; // חזרה לדובדבן
        currentFruitType = fruitSequence[currentFruitIndex];
        lastMoveTime = 0;
        fruitActive = false;
    }
    
    public void nextLevel() {
        // לא מאפס את הסדר של הפירות, רק את הזמנים
        lastMoveTime = System.currentTimeMillis();
        
        // אם יש פרי פעיל, תזוז אותו למקום חדש מיד
        if (currentFruit != null) {
            moveFruitToNewLocation();
        }
    }
    
    public Fruit getCurrentFruit() {
        return currentFruit;
    }
    
    public Fruit.FruitType getCurrentFruitType() {
        return currentFruitType;
    }
    
    public long getTimeUntilNextMove() {
        if (!fruitActive || currentFruit == null) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long timeSinceLastMove = currentTime - lastMoveTime;
        long moveInterval = getMoveIntervalForLevel();
        
        return Math.max(0, moveInterval - timeSinceLastMove);
    }
    
    public boolean isFruitActive() {
        return fruitActive && currentFruit != null && !currentFruit.isCollected();
    }
}
