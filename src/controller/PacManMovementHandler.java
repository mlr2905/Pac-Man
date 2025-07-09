package controller;

import entity.pacman.PacMan;
import view.game.GamePanel;

public class PacManMovementHandler {

    private PacMan pacMan;
    private GamePanel gp;
    private KeyHandler keyH;
    private String currentMovingDirection = "none";
    private String requestedDirection = "none";
    private boolean isTeleporting = false;
    private int teleportCooldownCounter = 0;
    private final int TELEPORT_COOLDOWN_FRAMES = 10;

    public PacManMovementHandler(PacMan pacMan, GamePanel gp, KeyHandler keyH) {
        this.pacMan = pacMan;
        this.gp = gp;
        this.keyH = keyH;
    }

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
        if (keyH.upPressed)
            return "up";
        if (keyH.downPressed)
            return "down";
        if (keyH.leftPressed)
            return "left";
        if (keyH.rightPressed)
            return "right";
        return "none";
    }

    private boolean canMoveInDirection(String dir) {
        int newX = pacMan.x, newY = pacMan.y;

        switch (dir) {
            case "up":
                newY -= pacMan.speed;
                break;
            case "down":
                newY += pacMan.speed;
                break;
            case "left":
                newX -= pacMan.speed;
                break;
            case "right":
                newX += pacMan.speed;
                break;
            default:
                return false;
        }

        return !checkCollision(newX, newY);
    }

    private boolean executeMovement() {
        if (currentMovingDirection.equals("none")) {
            return false;
        }

        int newX = pacMan.x, newY = pacMan.y;
        
        switch (currentMovingDirection) {
            case "up":
                newY -= pacMan.speed;
                pacMan.direction = 3;
                break;
            case "down":
                newY += pacMan.speed;
                pacMan.direction = 0;
                break;
            case "left":
                newX -= pacMan.speed;
                pacMan.direction = 2;
                break;
            case "right":
                newX += pacMan.speed;
                pacMan.direction = 1;
                break;
        }

        if (!checkCollision(newX, newY)) {
            pacMan.x = newX;
            pacMan.y = newY;
            return true;
        } else {
            currentMovingDirection = "none";
            return false;
        }
    }

    private boolean checkCollision(int newX, int newY) {
        int pacManSize = gp.tileSize;

        int leftBound = newX ;
        int rightBound = newX + pacManSize  - 1;
        int topBound = newY ;
        int bottomBound = newY + pacManSize  - 1;

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

        int pacManCol = (pacMan.x + gp.tileSize / 2) / gp.tileSize;
        int pacManRow = (pacMan.y + gp.tileSize / 2) / gp.tileSize;

        if (pacManCol < 0 || pacManCol >= gp.tileM.map[0].length || pacManRow < 0 || pacManRow >= gp.tileM.map.length) {
            return;
        } 

        if (gp.tileM.map[pacManRow][pacManCol] == 3) {
            if (pacManCol == gp.teleport1[0] && pacManRow == gp.teleport1[1]) {
                pacMan.x = gp.teleport2[0] * gp.tileSize;
                pacMan.y = gp.teleport2[1] * gp.tileSize;
            } else if (pacManCol == gp.teleport2[0] && pacManRow == gp.teleport2[1]) {
                pacMan.x = gp.teleport1[0] * gp.tileSize;
                pacMan.y = gp.teleport1[1] * gp.tileSize;
            }
            isTeleporting = true;
            teleportCooldownCounter = TELEPORT_COOLDOWN_FRAMES;
        }
    }
}