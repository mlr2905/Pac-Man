package controller.managers;

import java.awt.Rectangle;
import entity.ghost.Ghost;
import view.game.GamePanel;

public class CollisionManager {
    
    private GamePanel gp;

    public CollisionManager(GamePanel gp) {
        this.gp = gp;
    }

    public void checkCollision() {
        Rectangle pacManBounds = new Rectangle(gp.pacMan.x, gp.pacMan.y, gp.tileSize, gp.tileSize);
        Ghost[] allGhosts = {gp.blinky, gp.pinky, gp.inky, gp.clyde};

        for (Ghost ghost : allGhosts) {
            if (ghost == null) continue;
            Rectangle ghostBounds = new Rectangle(ghost.x, ghost.y, gp.tileSize, gp.tileSize);
            
            if (pacManBounds.intersects(ghostBounds)) {
                pacManHit();
                break;
            }
        }
    }

    private void pacManHit() {
        gp.lives--;

        if (gp.lives <= 0) {
            gp.levelManager.gameState = gp.levelManager.gameOverState;
        } else {
            gp.pacMan.setDefaultValues();
            gp.entityManager.resetAllGhostPositions();
        }
    }
}