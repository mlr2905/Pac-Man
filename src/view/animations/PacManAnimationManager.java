package view.animations;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import entity.pacman.PacMan;

public class PacManAnimationManager {

    private PacMan pacMan;
    private BufferedImage[][] frames;
    private int frameIndex = 0;
    private int frameTick = 0;
    private final int frameDelay = 8;
    private final int framesPerDir = 3;

    public PacManAnimationManager(PacMan pacMan) {
        this.pacMan = pacMan;
        loadpacManImages();
    }

    public void loadpacManImages() {
        try {
            BufferedImage down = ImageIO.read(getClass().getResourceAsStream("/view/resources/pacMan/pacmanDown.png"));
            BufferedImage right = ImageIO.read(getClass().getResourceAsStream("/view/resources/pacMan/pacmanRight.png"));
            BufferedImage left = ImageIO.read(getClass().getResourceAsStream("/view/resources/pacMan/pacmanLeft.png"));
            BufferedImage up = ImageIO.read(getClass().getResourceAsStream("/view/resources/pacMan/pacmanUp.png"));
            BufferedImage Closed = ImageIO.read(getClass().getResourceAsStream("/view/resources/pacMan/pacmanClosed.png"));

            frames = new BufferedImage[4][3];
            frames[0][0] = down;
            frames[0][1] = Closed;
            frames[0][2] = down;
            frames[1][0] = right;
            frames[1][1] = Closed;
            frames[1][2] = right;
            frames[2][0] = left;
            frames[2][1] = Closed;
            frames[2][2] = left;
            frames[3][0] = up;
            frames[3][1] = Closed;
            frames[3][2] = up;

            System.out.println("pacMan images loaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading pacMan images: " + e.getMessage());
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
        if (frames != null && pacMan.direction >= 0 && pacMan.direction < frames.length) {
            return frames[pacMan.direction][frameIndex];
        }
        return null;
    }
}