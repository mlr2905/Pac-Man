package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JPanel;

import collectibles.Collectable;
import entity.ghost.BlinkyTargetingStrategy;
import entity.ghost.ClydeTargetingStrategy;
import entity.ghost.ExitingHouseState;
import entity.ghost.Ghost;
import entity.ghost.TargetingStrategy;
import entity.player.Player;
import map.MapData;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 38;
    final int maxScreenRow = 19;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // GAME SYSTEM
    int FPS = 60;
    Thread gameThread;
    KeyHandler keyH = new KeyHandler();
    
    // MANAGERS & HANDLERS
    public TileManager tileM;
    public ScoreManager scoreM;
    public LevelManager levelManager;

    // ENTITIES & OBJECTS
    public Player player;
    public Ghost blinky; 
    public Ghost clyde;
    public ArrayList<Collectable> collectables = new ArrayList<>();
    public int[] teleport1 = null;
    public int[] teleport2 = null;

    // חדש: מערכת ניהול תור היציאה של הרוחות
    private Queue<Ghost> exitQueue = new LinkedList<>();
    private boolean isExitingLaneBusy = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Initialize managers
        tileM = new TileManager(this);
        scoreM = new ScoreManager(this);
        levelManager = new LevelManager(this);

        player = new Player(this, keyH);
        
        initializeGhosts(); 
        findTeleportTiles();
    }

    public void initializeGhosts() {
        // 1. הגדר את אסטרטגיות המרדף
        TargetingStrategy blinkyStrategy = new BlinkyTargetingStrategy();
TargetingStrategy clydeStrategy = new ClydeTargetingStrategy(8); // רדיוס 8, ללא נקודת מטרה
        // 2. הגדר את נקודת ההתחלה (זהה לשתיהן)
        int startTileX = 18;
        int startTileY = 7;

        // 3. צור את אובייקטי הרוחות עם הנתונים הייחודיים שלהן
        blinky = new Ghost(this, "blinky", startTileX * tileSize, startTileY * tileSize, 100, blinkyStrategy);
        clyde = new Ghost(this, "clyde", startTileX * tileSize, startTileY * tileSize, 200, clydeStrategy);
    }

    public void findTeleportTiles() {
        for (int row = 0; row < MapData.INITIAL_MAP_DATA.length; row++) {
            for (int col = 0; col < MapData.INITIAL_MAP_DATA[0].length; col++) {
                if (MapData.INITIAL_MAP_DATA[row][col] == 3) {
                    if (teleport1 == null) teleport1 = new int[]{col, row};
                    else if (teleport2 == null) teleport2 = new int[]{col, row};
                }
            }
        }
    }
    
    // --- מתודות חדשות לניהול יציאת הרוחות ---

    /**
     * מתודה שרוח קוראת לה כדי להצטרף לתור היציאה.
     */
    public void requestToExit(Ghost ghost) {
        if (!exitQueue.contains(ghost)) {
            exitQueue.add(ghost);
        }
    }

    /**
     * מעדכן את מצב "הרמזור" של נתיב היציאה. נקרא על ידי הרוח שסיימה לצאת.
     */
    public void setExitingLaneBusy(boolean busy) {
        this.isExitingLaneBusy = busy;
    }
    
    /**
     * הסדרן הראשי. בודק אם הנתיב פנוי ונותן לרוח הבאה בתור לצאת.
     */
    private void manageGhostExits() {
        // אם הנתיב לא תפוס ויש מישהו שממתין בתור
        if (!isExitingLaneBusy && !exitQueue.isEmpty()) {
            Ghost ghostToExit = exitQueue.poll(); // קח את הרוח הראשונה מהתור
            isExitingLaneBusy = true; // תפוס את הנתיב (הרמזור אדום)
            ghostToExit.setState(new ExitingHouseState()); // אמור לה להתחיל לצאת
        }
    }


    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void stopGame() {
        gameThread = null;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (levelManager.gameState == levelManager.playState) {
            player.update();
            for (Collectable item : collectables) {
                item.update();
            }
            blinky.update();
            clyde.update();
            
            // קריאה לסדרן בכל פריים כדי לבדוק אם אפשר לשחרר רוח נוספת
            manageGhostExits();
        }
        levelManager.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tileM.draw(g2);
        for (Collectable item : collectables) {
            item.draw(g2);
        }
        player.draw(g2);
        blinky.draw(g2); 
        clyde.draw(g2);
        scoreM.draw(g2, levelManager.getCurrentLevel());
        g2.dispose();
    }
}