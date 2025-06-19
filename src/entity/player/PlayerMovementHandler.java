package entity.player;

import main.GamePanel;
import main.KeyHandler;

public class PlayerMovementHandler {

    private Player player;
    private GamePanel gp;
    
    private KeyHandler keyH;

    // State related to movement
    private String currentMovingDirection = "none";
    private String requestedDirection = "none";
    private boolean isTeleporting = false;
    private int teleportCooldownCounter = 0;
    private final int TELEPORT_COOLDOWN_FRAMES = 10;

    public PlayerMovementHandler(Player player, GamePanel gp, KeyHandler keyH) {
        this.player = player;
        this.gp = gp;
        this.keyH = keyH;
    }

    /**
     * The main update method for movement logic.
     * @return true if the player moved, false otherwise.
     */
    public boolean update() {
        if (isTeleporting) {
            teleportCooldownCounter--;
            if (teleportCooldownCounter <= 0) {
                isTeleporting = false;
            }
        }

        String inputDirection = getInputDirection();
        if (!inputDirection.equals("none")) {
            requestedDirection = inputDirection;
        }

        if (!requestedDirection.equals("none") && canMoveInDirection(requestedDirection)) {
            currentMovingDirection = requestedDirection;
            requestedDirection = "none";
        }

        boolean moved = executeMovement();
        checkTeleport();
        
        return moved;
    }

    private String getInputDirection() {
        if (keyH.upPressed) return "up";
        if (keyH.downPressed) return "down";
        if (keyH.leftPressed) return "left";
        if (keyH.rightPressed) return "right";
        return "none";
    }

    private boolean canMoveInDirection(String dir) {
        int newX = player.x, newY = player.y;
        
        switch (dir) {
            case "up": newY -= player.speed; break;
            case "down": newY += player.speed; break;
            case "left": newX -= player.speed; break;
            case "right": newX += player.speed; break;
            default: return false;
        }
        
        return !checkCollision(newX, newY);
    }

    private boolean executeMovement() {
        if (currentMovingDirection.equals("none")) {
            return false;
        }

        int newX = player.x, newY = player.y;
        
        switch (currentMovingDirection) {
            case "up":
                newY -= player.speed;
                player.direction = 3;
                break;
            case "down":
                newY += player.speed;
                player.direction = 0;
                break;
            case "left":
                newX -= player.speed;
                player.direction = 2;
                break;
            case "right":
                newX += player.speed;
                player.direction = 1;
                break;
        }

        if (!checkCollision(newX, newY)) {
            player.x = newX;
            player.y = newY;
            return true;
        } else {
            currentMovingDirection = "none";
            return false;
        }
    }
    
    private boolean checkCollision(int newX, int newY) {
        int playerSize = gp.tileSize;
        int margin = 2;
        
        int leftBound = newX + margin;
        int rightBound = newX + playerSize - margin;
        int topBound = newY + margin;
        int bottomBound = newY + playerSize - margin;
        
        int leftTile = leftBound / gp.tileSize;
        int rightTile = rightBound / gp.tileSize;
        int topTile = topBound / gp.tileSize;
        int bottomTile = bottomBound / gp.tileSize;
        
        for (int row = topTile; row <= bottomTile; row++) {
            for (int col = leftTile; col <= rightTile; col++) {
                if (isWall(col, row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWall(int col, int row) {
        if (col < 0 || col >= gp.tileM.map[0].length || row < 0 || row >= gp.tileM.map.length) {
            return true;
        }
        return gp.tileM.map[row][col] == 1;
    }

    private void checkTeleport() {
        if (isTeleporting || gp.teleport1 == null || gp.teleport2 == null) {
            return;
        }

        int playerCol = (player.x + gp.tileSize / 2) / gp.tileSize;
        int playerRow = (player.y + gp.tileSize / 2) / gp.tileSize;

        if (playerCol < 0 || playerCol >= gp.tileM.map[0].length || playerRow < 0 || playerRow >= gp.tileM.map.length) {
            return;
        }

        if (gp.tileM.map[playerRow][playerCol] == 3) {
            if (playerCol == gp.teleport1[0] && playerRow == gp.teleport1[1]) {
                player.x = gp.teleport2[0] * gp.tileSize;
                player.y = gp.teleport2[1] * gp.tileSize;
            } else if (playerCol == gp.teleport2[0] && playerRow == gp.teleport2[1]) {
                player.x = gp.teleport1[0] * gp.tileSize;
                player.y = gp.teleport1[1] * gp.tileSize;
            }
            isTeleporting = true;
            teleportCooldownCounter = TELEPORT_COOLDOWN_FRAMES;
        }
    }

}