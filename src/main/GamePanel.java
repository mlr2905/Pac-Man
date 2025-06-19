package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import entity.player.Player;
import entity.Collectable;
import entity.ScoreManager;
import entity.ghost.Blinky;
import tile.TileManager;
import map.MapData;

import java.util.ArrayList;

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
    public Blinky blinky; // הצהרה על Blinky
    public ArrayList<Collectable> collectables = new ArrayList<>();
    public int[] teleport1 = null;
    public int[] teleport2 = null;

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
        blinky = new Blinky(this); // אתחול Blinky כאן
        findTeleportTiles();
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
            blinky.update(); // קריאה ל-update של Blinky
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
        scoreM.draw(g2, levelManager.getCurrentLevel());
        g2.dispose();
    }
}