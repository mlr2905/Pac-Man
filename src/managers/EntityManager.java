package managers;

import entity.ghost.Ghost;
import entity.ghost.strategy.BlinkyTargetingStrategy;
import entity.ghost.strategy.ClydeTargetingStrategy;
import entity.ghost.strategy.InkyTargetingStrategy;
import entity.ghost.strategy.PinkyTargetingStrategy;
import entity.ghost.strategy.TargetingStrategy;
import main.GamePanel;
import collectibles.Collectable;

public class EntityManager {

    private GamePanel gp;

    public EntityManager(GamePanel gp) {
        this.gp = gp;
    }

    public void initializeGhosts() {
        TargetingStrategy blinkyStrategy = new BlinkyTargetingStrategy();
        TargetingStrategy pinkyStrategy = new PinkyTargetingStrategy(4);
        TargetingStrategy inkyStrategy = new InkyTargetingStrategy();
        TargetingStrategy clydeStrategy = new ClydeTargetingStrategy(8);

        int startTileX = 18;
        int startTileY = 7;
        int tileSize = gp.tileSize;

        gp.blinky = new Ghost(gp, "blinky", startTileX * tileSize, startTileY * tileSize, 100, blinkyStrategy);
        gp.pinky = new Ghost(gp, "pinky", startTileX * tileSize, startTileY * tileSize, 200, pinkyStrategy);
        gp.inky = new Ghost(gp, "inky", startTileX * tileSize, startTileY * tileSize, 300, inkyStrategy);
        gp.clyde = new Ghost(gp, "clyde", startTileX * tileSize, startTileY * tileSize, 400, clydeStrategy);
    }
    
    public void resetAllGhostPositions() {
        int startTileX = 18;
        int startTileY = 7;
        int tileSize = gp.tileSize;

        gp.blinky.setDefaultValues(startTileX * tileSize, startTileY * tileSize);
        gp.pinky.setDefaultValues(startTileX * tileSize, startTileY * tileSize);
        gp.inky.setDefaultValues(startTileX * tileSize, startTileY * tileSize);
        gp.clyde.setDefaultValues(startTileX * tileSize, startTileY * tileSize);
    }

    public void update() {
        gp.pacMan.update();
        for (Collectable item : gp.collectables) {
            item.update();
        }
        gp.blinky.update();
        gp.pinky.update();
        gp.inky.update();
        gp.clyde.update();
    }

    public void draw(java.awt.Graphics2D g2) {
        for (Collectable item : gp.collectables) {
            item.draw(g2);
        }
        gp.pacMan.draw(g2);
        gp.blinky.draw(g2);
        gp.pinky.draw(g2);
        gp.inky.draw(g2);
        gp.clyde.draw(g2);
    }
}