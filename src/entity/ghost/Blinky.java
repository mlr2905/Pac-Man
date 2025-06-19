package entity.ghost; // ודא שכל קבצי הרוח תחת אותה חבילה

import main.GamePanel;
import map.MapData;
import entity.Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Blinky extends Entity {

       public GamePanel gp;
    public Rectangle solidArea;
    public BlinkyConfig config;

    public int lastPlayerTileX = -1; 
    public int lastPlayerTileY = -1; 

    private BlinkyState currentState;
    private AnimationManager animationManager;
    public int repathTimer = 0; // <-- הוסף את השורה הזאת

    private String currentMovingDirection = "none";
    private String requestedDirection = "none";
    private int directionForAnimation = 0; 
    private boolean gameStartedForBlinky = false;
    private Random random = new Random();

    public enum MovementRule {
        IN_HOUSE,
        THROUGH_HOUSE_DOOR,
        CHASE
    }

    public Blinky(GamePanel gp) {
        this.gp = gp;
        this.animationManager = new AnimationManager(8); // ודא שהקובץ AnimationManager.java קיים
        this.config = new BlinkyConfig();
        this.solidArea = new Rectangle(6, 6, gp.tileSize - 10, gp.tileSize - 10);
        setDefaultValues();
    }

    public void setDefaultValues() {
        x = config.START_IN_EIGHT_X * gp.tileSize;
        y = config.START_IN_EIGHT_Y * gp.tileSize;
        speed = 2;
        currentMovingDirection = "none";
        requestedDirection = "none";
        gameStartedForBlinky = false;
        setState(new WaitingState());
    }

    public void setState(BlinkyState newState) {
        this.currentState = newState;
    }

    public void setRequestedDirection(String dir) {
        this.requestedDirection = dir;
    }

    public String getCurrentMovingDirection() {
        return this.currentMovingDirection;
    }

    public void update() {
        if (!gameStartedForBlinky && gp.levelManager.gameState == gp.levelManager.playState) {
            gameStartedForBlinky = true;
        }
        if (!gameStartedForBlinky) {
            animationManager.update(false);
            return;
        }

        currentState.update(this);

      // פתח את הקובץ Blinky.java והחלף את השורה הנ"ל בזו:
boolean isMoving = !currentMovingDirection.equals("none") && canMoveInDirection(currentMovingDirection, ((BlinkyState)currentState).getClass().getSimpleName().contains("Chase") ? MovementRule.CHASE : ((BlinkyState)currentState).getClass().getSimpleName().contains("Exit") ? MovementRule.THROUGH_HOUSE_DOOR : MovementRule.IN_HOUSE);
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = animationManager.getCurrentFrame(directionForAnimation);
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
    }

    public boolean moveToTarget(int targetCol, int targetRow, MovementRule rule) {
        if (Math.abs(x - targetCol * gp.tileSize) < speed && Math.abs(y - targetRow * gp.tileSize) < speed) {
            x = targetCol * gp.tileSize;
            y = targetRow * gp.tileSize;
            return true;
        } else {
            requestedDirection = getDirectionToTile(targetCol, targetRow);
            executeMovement(rule);
            return false;
        }
    }

    // הפכנו אותה ל-public כדי ש-ChasingState יוכל להשתמש בה
    public String getDirectionToTile(int targetCol, int targetRow) {
        int diffX = targetCol * gp.tileSize - x;
        int diffY = targetRow * gp.tileSize - y;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            return diffX > 0 ? "right" : "left";
        } else {
            return diffY > 0 ? "down" : "up";
        }
    }

    // --- המתודה החשובה ביותר לתיקון ---
    public boolean executeMovement(MovementRule rule) {
        if (!"none".equals(requestedDirection)) {
            currentMovingDirection = requestedDirection;
        }

        if ("none".equals(currentMovingDirection)) {
            return false;
        }

        int newX = x, newY = y;
        switch (currentMovingDirection) {
            case "up":
                newY -= speed;
                directionForAnimation = 3;
                break;
            case "down":
                newY += speed;
                directionForAnimation = 0;
                break;
            case "left":
                newX -= speed;
                directionForAnimation = 2;
                break;
            case "right":
                newX += speed;
                directionForAnimation = 1;
                break;
            default:
                return false;
        }

        if (!checkCollision(newX, newY, rule)) {
            x = newX;
            y = newY;
            return true;
        } else {
            // לא מאפסים את הכיוון! זה המפתח לתיקון.
            return false;
        }
    }

    private boolean checkCollision(int newX, int newY, MovementRule rule) {
        Rectangle futureSolidArea = new Rectangle(newX + solidArea.x, newY + solidArea.y, solidArea.width,
                solidArea.height);
        int leftTile = futureSolidArea.x / gp.tileSize;
        int rightTile = (futureSolidArea.x + futureSolidArea.width) / gp.tileSize;
        int topTile = futureSolidArea.y / gp.tileSize;
        int bottomTile = (futureSolidArea.y + futureSolidArea.height) / gp.tileSize;

        if (leftTile < 0 || rightTile >= MapData.INITIAL_MAP_DATA[0].length || topTile < 0
                || bottomTile >= MapData.INITIAL_MAP_DATA.length) {
            return true;
        }

        for (int row = topTile; row <= bottomTile; row++) {
            for (int col = leftTile; col <= rightTile; col++) {
                int tileValue = MapData.INITIAL_MAP_DATA[row][col];
                boolean isWall;
                switch (rule) {
                    case IN_HOUSE:
                        isWall = (tileValue != 6 && tileValue != 8);
                        break;
                    case THROUGH_HOUSE_DOOR:
                        isWall = (tileValue != 6 && tileValue != 8 && tileValue != 4 && tileValue != 0);
                        break;
                    case CHASE:
                        isWall = !(tileValue == 0 || tileValue == 2 || tileValue == 7);
                        break;
                    default:
                        isWall = true;
                        break;
                }
                if (isWall)
                    return true;
            }
        }
        return false;
    }

    public boolean canMoveInDirection(String dir, MovementRule rule) {
        if ("none".equals(dir))
            return false;
        int newX = x, newY = y;
        switch (dir) {
            case "up":
                newY -= speed;
                break;
            case "down":
                newY += speed;
                break;
            case "left":
                newX -= speed;
                break;
            case "right":
                newX += speed;
                break;
            default:
                return false;
        }
        return !checkCollision(newX, newY, rule);
    }

    public void determineRandomDirectionInMaze() {
        determineRandomDirection(MovementRule.CHASE);
    }

    public void determineRandomDirectionInHouse() {
        determineRandomDirection(MovementRule.IN_HOUSE);
    }

   private void determineRandomDirection(MovementRule rule) {
    // הוספת תנאי זה היא התיקון המרכזי.
    // הוא דואג שנבחר כיוון חדש רק כשהרוח נעצרת או נתקעת בקיר.
    if ("none".equals(currentMovingDirection) || !canMoveInDirection(currentMovingDirection, rule)) {
        
        ArrayList<String> possibleDirections = new ArrayList<>();
        String[] allDirections = {"up", "down", "left", "right"};
        for (String dir : allDirections) {
            if (canMoveInDirection(dir, rule)) {
                possibleDirections.add(dir);
            }
        }
        
        if (!possibleDirections.isEmpty()) {
            String opposite = getOppositeDirection(currentMovingDirection);
            // מנסה להימנע מחזרה על עקבותיו, אלא אם אין ברירה
            if (possibleDirections.size() > 1) {
                possibleDirections.remove(opposite);
            }
            requestedDirection = possibleDirections.get(random.nextInt(possibleDirections.size()));
        } else {
            // כמוצא אחרון אם נתקעים בפינה, מנסים לחזור אחורה
            requestedDirection = getOppositeDirection(currentMovingDirection);
        }
    }
}
    private String getOppositeDirection(String dir) {
        switch (dir) {
            case "up":
                return "down";
            case "down":
                return "up";
            case "left":
                return "right";
            case "right":
                return "left";
            default:
                return "none";
        }
    }
}
