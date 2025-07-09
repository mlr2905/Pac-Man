package view.game;

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
import java.util.List;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import collectibles.Collectable;
import controller.KeyHandler;
import controller.managers.EntityManager;
import controller.managers.FruitManager;
import controller.managers.LevelManager;
import controller.managers.PowerPelletManager;
import controller.managers.ScoreManager;
import controller.managers.SoundManager;
import controller.managers.HighScoreManager;
import controller.strategy.BlinkyTargetingStrategy;
import controller.strategy.ClydeTargetingStrategy;
import controller.strategy.InkyTargetingStrategy;
import controller.strategy.PinkyTargetingStrategy;
import controller.strategy.TargetingStrategy;
import entity.ghost.Ghost;
import entity.pacman.PacMan;
import entity.state.ExitingHouseState;
import entity.state.FrightenedState;
import map.MapData;
import view.tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    final int originalTileSize = 15;
    final int scale = 2;
    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 38;
    public final int maxScreenRow = 19;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    private long lastPelletSoundTime = 0;
    private final long PELLET_SOUND_COOLDOWN = 1000;
    private long lastGhostExitSoundTime = 0;
    private final long GHOST_EXIT_SOUND_COOLDOWN = 5500;
    private SoundManager soundManager;
    int FPS = 60;
    Thread gameThread;
    public KeyHandler keyH;

    public TileManager tileM;
    public ScoreManager scoreM;
    public LevelManager levelManager;
    public PowerPelletManager powerPelletManager;
    public FruitManager fruitManager;
    public FrightenedState frightenedState;
    public int lives;
    public PacMan pacMan;
    public Ghost blinky;
    public Ghost pinky;
    public Ghost inky;
    public Ghost clyde;
    public ArrayList<Collectable> collectables = new ArrayList<>();
    public int[] teleport1 = null;
    public int[] teleport2 = null;
    private int timefrightened;
    public int speedGhost = 2;
    public BufferedImage lifeImage;

    private Queue<Ghost> exitQueue = new LinkedList<>();
    private boolean isExitingLaneBusy = false;
    public EntityManager entityManager;

    // הוספות למערכת השיאים
    private HighScoreManager highScoreManager;
    private boolean gameEndedWithHighScore = false;
    private String playerName = "";
    private boolean enteringName = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.gray);
        this.setDoubleBuffered(true);

        // יצירת KeyHandler עם הפניה לGamePanel
        keyH = new KeyHandler(this);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        tileM = new TileManager(this);
        scoreM = new ScoreManager(this);
        levelManager = new LevelManager(this);
        powerPelletManager = new PowerPelletManager();
        fruitManager = new FruitManager(this);
        frightenedState = new FrightenedState();
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

    // מתודות למערכת השיאים
    public void setHighScoreManager(HighScoreManager highScoreManager) {
        this.highScoreManager = highScoreManager;
    }

    public boolean isEnteringName() {
        return enteringName;
    }

    public void handleNameInput(char keyChar) {
        if (keyChar == '\n' || keyChar == '\r') { // Enter pressed
            if (!playerName.trim().isEmpty()) {
                highScoreManager.addHighScore(playerName.trim(), scoreM.getScore());
                enteringName = false;
                gameEndedWithHighScore = false;
                levelManager.gameState = levelManager.gameOverState;
                System.out.println("High score saved for: " + playerName);
            }
        } else if (keyChar == '\b') { // Backspace
            if (playerName.length() > 0) {
                playerName = playerName.substring(0, playerName.length() - 1);
            }
        } else if (Character.isLetterOrDigit(keyChar) || keyChar == ' ' ||
                (keyChar >= 0x0590 && keyChar <= 0x05FF)) { // תמיכה בעברית
            if (playerName.length() < 20) { // הגבלת אורך השם
                playerName += keyChar;
            }
        }
    }

    private void checkForHighScore() {
        int finalScore = scoreM.getScore();

        if (highScoreManager != null && highScoreManager.isHighScore(finalScore)) {
            gameEndedWithHighScore = true;
            enteringName = true;
            playerName = "";
            this.requestFocus();
            System.out.println("New High Score! Enter your name...");
        } else {
            levelManager.gameState = levelManager.gameOverState;
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
        pinky = new Ghost(this, "pinky", startTileX * tileSize, startTileY * tileSize, 350, pinkyStrategy);
        inky = new Ghost(this, "inky", startTileX * tileSize, startTileY * tileSize, 700, inkyStrategy);
        clyde = new Ghost(this, "clyde", startTileX * tileSize, startTileY * tileSize, 950, clydeStrategy);
    }

    public List<Ghost> getAllGhosts() {
        List<Ghost> allGhosts = new ArrayList<>();
        allGhosts.add(blinky);
        allGhosts.add(pinky);
        allGhosts.add(inky);
        allGhosts.add(clyde);
        return allGhosts;
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

  public void manageGhostExits() {
    if (!isExitingLaneBusy && !exitQueue.isEmpty()) {
        Ghost ghostToExit = exitQueue.poll();
        isExitingLaneBusy = true;
        
        // בדיקה אם עברו 6 שניות מהצליל האחרון
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastGhostExitSoundTime >= GHOST_EXIT_SOUND_COOLDOWN) {
            SoundManager.getInstance().playSound("/view/resources/sounds/GhostChasing.wav");
            lastGhostExitSoundTime = currentTime;
        }
        
        ghostToExit.setState(new ExitingHouseState());
    }
}

    private void checkCollision() {
        Rectangle pacManBounds = new Rectangle(pacMan.x + pacMan.solidArea.x, pacMan.y + pacMan.solidArea.y,
                pacMan.solidArea.width, pacMan.solidArea.height);
        Ghost[] allGhosts = { blinky, pinky, inky, clyde };

        for (Ghost ghost : allGhosts) {
            if (ghost == null)
                continue;
            Rectangle ghostBounds = new Rectangle(ghost.x + ghost.solidArea.x, ghost.y + ghost.solidArea.y,
                    ghost.solidArea.width, ghost.solidArea.height);

            if (pacManBounds.intersects(ghostBounds)) {
                if (ghost.isFrightened() && !ghost.isEaten()) {

                    ghost.setEaten(true);
                    SoundManager.getInstance().playSound("/view/resources/sounds/eatingGhosts.wav");

                    int score = powerPelletManager.getGhostEatenScore();
                    scoreM.addScore(score);
                    System.out.println("Ghost eaten! Score: " + score);
                } else if (!ghost.isFrightened() && !ghost.isEaten()) {
                    SoundManager.getInstance().playSound("/view/resources/sounds/eatingGhosts.wav");

                    pacManHit();
                    break;
                }
            }
        }
    }

    public void pacManHit() {
        lives--;

        if (lives <= 0) {
            SoundManager.getInstance().playSound("/view/resources/sounds/gameover.wav");

            checkForHighScore(); // בדיקת שיא במקום הקצאה ישירה למצב גיים אובר
        } else {
            pacMan.setDefaultValues();
            GhostsetDefaultValues(speedGhost);
        }
    }

    public void GhostsetDefaultValues(int speed) {
        int startTileX = 18;
        int startTileY = 7;
        blinky.setDefaultValues(startTileX * tileSize, startTileY * tileSize, speed);
        pinky.setDefaultValues(startTileX * tileSize, startTileY * tileSize, speed);
        inky.setDefaultValues(startTileX * tileSize, startTileY * tileSize, speed);
        clyde.setDefaultValues(startTileX * tileSize, startTileY * tileSize, speed);
    }

    public void restartGame() {
        this.lives = 3;
        this.scoreM.reset();
        this.powerPelletManager.resetGhostEatenCount();
        this.fruitManager.reset();

        // איפוס רמה ל-1
        levelManager.currentLevel = 1;
        speedGhost = 2; // איפוס המהירות

        this.pacMan.setDefaultValues();
        for (Collectable item : collectables) {
            if (item instanceof collectibles.Pellet || item instanceof collectibles.PowerPellet) {
                item.setCollected(false);
                if (item instanceof collectibles.Pellet) {
                    ((collectibles.Pellet) item).setTemporarilyHidden(false);
                }
            }
        }
        initializeGhosts();

        exitQueue.clear();
        isExitingLaneBusy = false;

        // איפוס משתני השיאים
        gameEndedWithHighScore = false;
        enteringName = false;
        playerName = "";

        levelManager.gameState = levelManager.playState;
        System.out.println("Game restarted! Level 1 started!");
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

    public FrightenedState createFrightenedState() {

        return new FrightenedState();
    }

    public void update() {
        // אם נמצאים במצב הכנסת שם, לא לעדכן כלום
        if (enteringName) {
            return;
        }

        if (levelManager.gameState == levelManager.playState) {
            pacMan.update();
            for (Collectable item : collectables) {
                item.update();
            }
            blinky.update();
            pinky.update();
            inky.update();
            clyde.update();

            fruitManager.update();
            manageGhostExits();
            checkCollision();
            checkLevelComplete();
        }

        // לא לעדכן את levelManager כשנמצאים במצב הכנסת שם
        if (!enteringName) {
            levelManager.update();
        }
    }

    private void checkLevelComplete() {
        boolean allPelletsCollected = true;

        for (Collectable item : collectables) {
            if ((item instanceof collectibles.Pellet || item instanceof collectibles.PowerPellet)
                    && !item.isCollected()) {
                allPelletsCollected = false;
                break;
            }
        }

        if (allPelletsCollected) {
            System.out.println("Level Complete! All pellets collected!");
            lives++;
            advanceToNextLevel();
        }
    }

    private void advanceToNextLevel() {
        levelManager.currentLevel++;
        speedGhost++;
        if (levelManager.currentLevel > 3) {
            SoundManager.getInstance().playSound("/view/resources/sounds/gameover.wav");

            checkForHighScore();
            System.out.println("Game Complete! You Win!");
        } else {
            // מעבר לשלב הבא
            System.out.println("Advancing to level " + levelManager.currentLevel);

            for (Collectable item : collectables) {
                if (item instanceof collectibles.Pellet || item instanceof collectibles.PowerPellet) {
                    item.setCollected(false);
                    if (item instanceof collectibles.Pellet) {
                        ((collectibles.Pellet) item).setTemporarilyHidden(false);
                    }
                }
            }

            pacMan.setDefaultValues();
            GhostsetDefaultValues(speedGhost);
            fruitManager.nextLevel();
            powerPelletManager.resetGhostEatenCount();

            System.out.println("Level " + levelManager.currentLevel + " started!");
        }
    }

    private void drawLives(Graphics2D g2) {
        if (lifeImage == null)
            return;
        for (int i = 0; i < lives; i++) {
            g2.drawImage(lifeImage, tileSize * (i + 1), 540, tileSize, 30, null);
        }
    }

    private void drawFruitInfo(Graphics2D g2) {
        if (fruitManager.isFruitActive()) {
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.setColor(Color.YELLOW);

            String fruitInfo = "Current Fruit: " + fruitManager.getCurrentFruitType().name() +
                    " (" + fruitManager.getCurrentFruitType().getPoints() + " pts)";
            g2.drawString(fruitInfo, 500, 15);

            long timeUntilMove = fruitManager.getTimeUntilNextMove() / 1000;
            String timeInfo = "Next move in: " + timeUntilMove + "s";
            g2.drawString(timeInfo, 300, 15);

            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(Color.CYAN);

        } else {
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.setColor(Color.WHITE);
            g2.drawString("No fruit active", 10, 30);
        }
    }

    private void drawGameOverScreen(String text, Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.red);

        int x = getXforCenteredText(text, g2);
        int y = screenHeight / 2;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setColor(Color.white);
        text = "Press Enter to Restart";
        x = getXforCenteredText(text, g2);
        g2.drawString(text, x, y + 60);
    }

    public long getLastPelletSoundTime() {
        return lastPelletSoundTime;
    }

    public void setLastPelletSoundTime(long time) {
        this.lastPelletSoundTime = time;
    }

    public long getPelletSoundCooldown() {
        return PELLET_SOUND_COOLDOWN;
    }

    // מתודה חדשה לציור מסך הכנסת שם
    private void drawNameInputScreen(Graphics2D g2) {
        // רקע
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // כותרת
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        g2.setColor(Color.YELLOW);
        String title = "NEW HIGH SCORE!";
        int x = getXforCenteredText(title, g2);
        int y = screenHeight / 2 - 100;
        g2.drawString(title, x, y);

        // תצוגת ניקוד
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.setColor(Color.WHITE);
        String scoreText = "Score: " + scoreM.getScore();
        x = getXforCenteredText(scoreText, g2);
        g2.drawString(scoreText, x, y + 60);

        // בקשת שם
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        String prompt = "Enter your name:";
        x = getXforCenteredText(prompt, g2);
        g2.drawString(prompt, x, y + 120);

        // תיבת טקסט
        g2.setColor(Color.WHITE);
        g2.fillRect(screenWidth / 2 - 150, y + 140, 300, 40);
        g2.setColor(Color.BLACK);
        g2.drawRect(screenWidth / 2 - 150, y + 140, 300, 40);

        // הצגת השם שנכתב - פונט שתומך בעברית
        g2.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g2.setColor(Color.BLACK);
        String displayName = playerName + "_"; // קו תחתון מהבהב
        g2.drawString(displayName, screenWidth / 2 - 140, y + 165);

        // הוראות
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.setColor(Color.CYAN);
        String instructions = "Press ENTER to confirm, BACKSPACE to delete";
        x = getXforCenteredText(instructions, g2);
        g2.drawString(instructions, x, y + 220);
    }

    private int getXforCenteredText(String text, Graphics2D g2) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return screenWidth / 2 - length / 2;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (levelManager.gameState != levelManager.transitionState && !enteringName) {
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
            drawFruitInfo(g2);
        }

        if (enteringName) {
            drawNameInputScreen(g2);
        } else if (levelManager.gameState == levelManager.gameOverState) {
            if (levelManager.currentLevel == 4) {
                drawGameOverScreen("YOU WIN", g2);
            } else {
                drawGameOverScreen("GAME OVER", g2);
            }
        }

        g2.dispose();
    }
}