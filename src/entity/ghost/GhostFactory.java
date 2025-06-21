package entity.ghost;

import main.GamePanel;

public class GhostFactory {

    public static Ghost createBlinky(GamePanel gp) {
        // Blinky (red) - exits at score 100
        return new Ghost(gp, "blinky", 18 * gp.tileSize, 7 * gp.tileSize, 100, new BlinkyTargetingStrategy());
    }

    public static Ghost createClyde(GamePanel gp) {
        ClydeConfig config = new ClydeConfig();
        return new Ghost(gp, "clyde", config.START_IN_EIGHT_X * gp.tileSize, config.START_IN_EIGHT_Y * gp.tileSize, 200, new ClydeTargetingStrategy(8)); // 8 is the chase radius
    }
    
    public static Ghost createInky(GamePanel gp) {
        InkyConfig config = new InkyConfig();
        return new Ghost(gp, "inky", config.START_IN_EIGHT_X * gp.tileSize, config.START_IN_EIGHT_Y * gp.tileSize, 300, new InkyTargetingStrategy());
    }

    public static Ghost createPinky(GamePanel gp) {
        
        return new Ghost(gp, "pinky", 18 * gp.tileSize, 8 * gp.tileSize, 400, new PinkyTargetingStrategy(4)); // 4 tiles ahead ambush
    }
}