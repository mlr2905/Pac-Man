package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import collectibles.Collectable;
import controller.KeyHandler;
import controller.strategy.BlinkyTargetingStrategy;
import controller.strategy.ClydeTargetingStrategy;
import controller.strategy.InkyTargetingStrategy;
import controller.strategy.PinkyTargetingStrategy;
import controller.strategy.TargetingStrategy;
import entity.ghost.Ghost;
import entity.pacman.PacMan;
import entity.state.ExitingHouseState;
import managers.EntityManager;
import managers.LevelManager;
import managers.ScoreManager;
import map.MapData;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 38;
    public final int maxScreenRow = 19;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    int FPS = 60;
    Thread gameThread;
    public KeyHandler keyH = new KeyHandler();

    public TileManager tileM;
    public ScoreManager scoreM;
    public LevelManager levelManager;
    public int lives;

    public PacMan pacMan;
    public Ghost blinky;
    public Ghost pinky;
    public Ghost inky;
    public Ghost clyde;
    public ArrayList<Collectable> collectables = new ArrayList<>();
    public int[] teleport1 = null;
    public int[] teleport2 = null;

    public BufferedImage lifeImage;

    private Queue<Ghost> exitQueue = new LinkedList<>();
    private boolean isExitingLaneBusy = false;
    public EntityManager entityManager;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        tileM = new TileManager(this);
        scoreM = new ScoreManager(this);
        levelManager = new LevelManager(this);
        lives = 3;
        pacMan = new PacMan(this, keyH);

        initializeGhosts();
        findTeleportTiles();

        try {
            lifeImage = ImageIO.read(getClass().getResourceAsStream("/view/resources/PacMan/pacmanRight.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading life image!");
        }
    }

    public void initializeGhosts() {
        TargetingStrategy blinkyStrategy = new BlinkyTargetingStrategy();
        TargetingStrategy pinkyStrategy = new PinkyTargetingStrategy(4);
        TargetingStrategy inkyStrategy = new InkyTargetingStrategy();
        TargetingStrategy clydeStrategy = new ClydeTargetingStrategy(8);

        int startTileX = 18;
        int startTileY = 7;

        blinky = new Ghost(this, "blinky", startTileX * tileSize, startTileY * tileSize, 100, blinkyStrategy);
        pinky = new Ghost(this, "pinky", startTileX * tileSize, startTileY * tileSize, 200, pinkyStrategy);
        inky = new Ghost(this, "inky", startTileX * tileSize, startTileY * tileSize, 300, inkyStrategy);
        clyde = new Ghost(this, "clyde", startTileX * tileSize, startTileY * tileSize, 400, clydeStrategy);
    }

    public void findTeleportTiles() {
        for (int row = 0; row < MapData.INITIAL_MAP_DATA.length; row++) {
            for (int col = 0; col < MapData.INITIAL_MAP_DATA[0].length; col++) {
                if (MapData.INITIAL_MAP_DATA[row][col] == 3) {
                    if (teleport1 == null)
                        teleport1 = new int[] { col, row };
                    else if (teleport2 == null)
                        teleport2 = new int[] { col, row };
                }
            }
        }
    }

    public void requestToExit(Ghost ghost) {
        if (!exitQueue.contains(ghost)) {
            exitQueue.add(ghost);
        }
    }

    public void setExitingLaneBusy(boolean busy) {
        this.isExitingLaneBusy = busy;
    }

    private void manageGhostExits() {
        if (!isExitingLaneBusy && !exitQueue.isEmpty()) {
            Ghost ghostToExit = exitQueue.poll();
            isExitingLaneBusy = true;
            ghostToExit.setState(new ExitingHouseState());
        }
    }

    private void checkCollision() {
        Rectangle pacManBounds = new Rectangle(pacMan.x, pacMan.y, tileSize, tileSize);
        Ghost[] allGhosts = { blinky, pinky, inky, clyde };

        for (Ghost ghost : allGhosts) {
            if (ghost == null)
                continue;
            Rectangle ghostBounds = new Rectangle(ghost.x, ghost.y, tileSize, tileSize);

            if (pacManBounds.intersects(ghostBounds)) {
                pacManHit();
                break;
            }
        }
    }

    public void pacManHit() {
        lives--;

        if (lives <= 0) {
            levelManager.gameState = levelManager.gameOverState;
        } else {
            pacMan.setDefaultValues();

            int startTileX = 18;
            int startTileY = 7;
            blinky.setDefaultValues(startTileX * tileSize, startTileY * tileSize);
            pinky.setDefaultValues(startTileX * tileSize, startTileY * tileSize);
            inky.setDefaultValues(startTileX * tileSize, startTileY * tileSize);
            clyde.setDefaultValues(startTileX * tileSize, startTileY * tileSize);
        }
    }

    public void restartGame() {
        this.lives = 3;

        this.scoreM.reset();

        this.pacMan.setDefaultValues();
 for (Collectable item : collectables) {
        item.setCollected(false);
    }
        initializeGhosts();

        exitQueue.clear();
        isExitingLaneBusy = false;

        levelManager.gameState = levelManager.playState;
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
            pacMan.update();
            for (Collectable item : collectables) {
                item.update();
            }
            blinky.update();
            pinky.update();
            inky.update();
            clyde.update();

            manageGhostExits();
            checkCollision();
        }

        levelManager.update();
    }

    private void drawLives(Graphics2D g2) {
        if (lifeImage == null)
            return;
        for (int i = 0; i < lives; i++) {
            g2.drawImage(lifeImage, tileSize * (i + 1), screenHeight - tileSize - 10, tileSize, tileSize, null);
        }
    }

    private void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.red);

        String text = "GAME OVER";
        int x = getXforCenteredText(text, g2);
        int y = screenHeight / 2;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setColor(Color.white);
        text = "Press Enter to Restart";
        x = getXforCenteredText(text, g2);
        g2.drawString(text, x, y + 60);
    }

    private int getXforCenteredText(String text, Graphics2D g2) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return screenWidth / 2 - length / 2;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (levelManager.gameState != levelManager.transitionState) {
            tileM.draw(g2);
            for (Collectable item : collectables) {
                item.draw(g2);
            }
            pacMan.draw(g2);
            blinky.draw(g2);
            pinky.draw(g2);
            inky.draw(g2);
            clyde.draw(g2);
            scoreM.draw(g2, levelManager.getCurrentLevel());
            drawLives(g2);
        }

        if (levelManager.gameState == levelManager.gameOverState) {
            drawGameOverScreen(g2);
        }

        g2.dispose();
    }
}