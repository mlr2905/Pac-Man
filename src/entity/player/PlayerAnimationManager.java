package entity.player;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class PlayerAnimationManager {

    private Player player;

    private BufferedImage[][] frames;
    private int frameIndex = 0;
    private int frameTick = 0;
    private final int frameDelay = 8;
    private final int framesPerDir = 3;

    public PlayerAnimationManager(Player player) {
        this.player = player;
        loadPlayerImages();
    }

    public void loadPlayerImages() {
        try {
            BufferedImage down1 = ImageIO.read(getClass().getResourceAsStream("/resources/player/pacmanDown.png"));
            BufferedImage down2 = ImageIO.read(getClass().getResourceAsStream("/resources/player/pacmanClosed.png"));
            BufferedImage right1 = ImageIO.read(getClass().getResourceAsStream("/resources/player/pacmanRight.png"));
            BufferedImage left1 = ImageIO.read(getClass().getResourceAsStream("/resources/player/pacmanLeft.png"));
            BufferedImage up1 = ImageIO.read(getClass().getResourceAsStream("/resources/player/pacmanUp.png"));

            frames = new BufferedImage[4][3];
            frames[0][0] = down1;
            frames[0][1] = down2;
            frames[0][2] = down1;
            frames[1][0] = right1;
            frames[1][1] = down2;
            frames[1][2] = right1;
            frames[2][0] = left1;
            frames[2][1] = down2;
            frames[2][2] = left1;
            frames[3][0] = up1;
            frames[3][1] = down2;
            frames[3][2] = up1;

            System.out.println("Player images loaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading player images: " + e.getMessage());
        }
    }

    public void updateAnimation(boolean isMoving) {
        if (isMoving) {
            frameTick++;
            if (frameTick >= frameDelay) {
                frameIndex = (frameIndex + 1) % framesPerDir;
                frameTick = 0;
            }
        } else {
            frameIndex = 0;
            frameTick = 0;
        }
    }

    public BufferedImage getFrame() {
        if (frames != null && player.direction >= 0 && player.direction < frames.length) {
            return frames[player.direction][frameIndex];
        }
        return null;
    }
}