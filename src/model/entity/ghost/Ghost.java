package entity.ghost;

import map.MapData;
import view.GamePanel;
import view.animations.GhostAnimationManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import controller.strategy.TargetingStrategy;
import entity.Entity;
import entity.state.GhostState;
import entity.state.WaitingState;
import entity.state.ReturningHomeState;

public class Ghost extends Entity {

    public GamePanel gp;
    public Rectangle solidArea;
    private GhostState currentState;
    private GhostAnimationManager animationManager;
    private Random random = new Random();

    public final int exitScoreTrigger;
    public final TargetingStrategy targetingStrategy;
    private boolean hasRequestedExit = false;
    public int lastpacManTileX = -1;
    public int lastpacManTileY = -1;
    public final int RETURN_TARGET_X = 18;
    public final int RETURN_TARGET_Y = 7;
    public final int EXIT_TILE_X = 18;
    public final int EXIT_TILE_Y = 6;
    public final int TILE_ABOVE_EXIT_X = 18;
    public final int TILE_ABOVE_EXIT_Y = 5;

    private String currentMovingDirection = "none";
    private String requestedDirection = "none";
    private int directionForAnimation = 0;
    private boolean gameStartedForGhost = false;
    
    // Frightened mode variables
    private boolean frightenedMode = false;
    private boolean eaten = false;
    private boolean returningHome = false;
    private boolean frightenedFrameAlternate = false;

    public enum MovementRule {
        IN_HOUSE, THROUGH_HOUSE_DOOR, CHASE
    }

    public Ghost(GamePanel gp, String name, int startX, int startY, int exitScoreTrigger,
            TargetingStrategy targetingStrategy) {
        this.gp = gp;
        this.animationManager = new GhostAnimationManager(name, 8);
        this.solidArea = new Rectangle(6, 6, gp.tileSize - 10, gp.tileSize - 10);

        this.exitScoreTrigger = exitScoreTrigger;
        this.targetingStrategy = targetingStrategy;
        
        setDefaultValues(startX, startY);
    }
    


    public void setDefaultValues(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        speed = 2;
        currentMovingDirection = "none";
        requestedDirection = "none";
        gameStartedForGhost = false;
        frightenedMode = false;
        eaten = false;
        returningHome = false;
        frightenedFrameAlternate = false;

        this.hasRequestedExit = false;

        setState(new WaitingState()); 
    }

    public void setState(GhostState newState) {
        System.out.println("Ghost state changing to: " + newState.getClass().getSimpleName()); // Debug
        this.currentState = newState;
        if (newState instanceof ReturningHomeState) {
            returningHome = true;
        } else if (newState instanceof WaitingState) {
            returningHome = false;
        }
    }

    public boolean hasRequestedExit() {
        return hasRequestedExit;
    }

    public void setHasRequestedExit(boolean requested) {
        this.hasRequestedExit = requested;
    }
    
    public void setFrightenedMode(boolean frightened) {
        this.frightenedMode = frightened;
        if (!frightened) {
            frightenedFrameAlternate = false;
        }
    }
    
    public boolean isFrightened() {
        return frightenedMode;
    }
    
    public void setEaten(boolean eaten) {
        this.eaten = eaten;
        if (eaten) {
            this.frightenedMode = false; // חשוב: כבה את מצב הפחד
            System.out.println("Ghost is being eaten! Setting state to ReturningHomeState"); // Debug
            setState(new ReturningHomeState());
        }
    }
    
    public boolean isEaten() {
        return eaten;
    }
    
    public boolean isReturningHome() {
        return returningHome;
    }
    
    public void switchFrightenedFrame() {
        frightenedFrameAlternate = !frightenedFrameAlternate;
    }

    public void update() {
        if (!gameStartedForGhost && gp.levelManager.gameState == gp.levelManager.playState) {
            gameStartedForGhost = true;
        }
        if (!gameStartedForGhost) {
            animationManager.update(false);
            return;
        }

        currentState.update(this);

        boolean isMoving = !currentMovingDirection.equals("none");
        animationManager.update(isMoving);
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        
        if (eaten) {
            // Show eyes when eaten - this takes priority over everything else
            image = animationManager.getEyesFrame(directionForAnimation);
            System.out.println("Drawing eyes frame for direction: " + directionForAnimation); // Debug
        } else if (frightenedMode) {
            // Check if we should show blinking frames (when frightened time is almost up)
            if (currentState instanceof entity.state.FrightenedState && 
                ((entity.state.FrightenedState) currentState).isBlinking()) {
                // Show blinking frames
                image = animationManager.getBlinkingFrame(frightenedFrameAlternate);
            } else {
                // Show regular frightened frames
                image = animationManager.getFrightenedFrame(frightenedFrameAlternate);
            }
        } else {
            // Use normal animation frames
            image = animationManager.getCurrentFrame(directionForAnimation);
        }
        
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        } else {
            System.err.println("Warning: No image to draw for ghost state - eaten: " + eaten + ", frightened: " + frightenedMode);
        }
    }

    public GhostAnimationManager getAnimationManager() {
        return this.animationManager;
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

    public String getDirectionToTile(int targetCol, int targetRow) {
        int diffX = targetCol * gp.tileSize - x;
        int diffY = targetRow * gp.tileSize - y;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            return diffX > 0 ? "right" : "left";
        } else {
            return diffY > 0 ? "down" : "up";
        }
    }

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
                        isWall = !(tileValue == 0 || tileValue == 2 || tileValue == 7 || (tileValue == 4 && eaten)); // Allow tile 4 when eaten
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
        if ("none".equals(currentMovingDirection) || !canMoveInDirection(currentMovingDirection, rule)) {

            ArrayList<String> possibleDirections = new ArrayList<>();
            String[] allDirections = { "up", "down", "left", "right" };
            for (String dir : allDirections) {
                if (canMoveInDirection(dir, rule)) {
                    possibleDirections.add(dir);
                }
            }

            if (!possibleDirections.isEmpty()) {
                String opposite = getOppositeDirection(currentMovingDirection);
                if (possibleDirections.size() > 1) {
                    possibleDirections.remove(opposite);
                }
                requestedDirection = possibleDirections.get(random.nextInt(possibleDirections.size()));
            } else {
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

    public void setRequestedDirection(String dir) {
        this.requestedDirection = dir;
    }

    public String getCurrentMovingDirection() {
        return this.currentMovingDirection;
    }
}