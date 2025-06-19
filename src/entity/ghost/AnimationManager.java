package entity.ghost;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class AnimationManager {

    private BufferedImage[][] frames;
    private int frameIndex = 0;
    private int frameTick = 0;
    private final int frameDelay;
    // ודא שהמשתנה הזה קיים
    private final String ghostName;

    /**
     * הבנאי חייב לקבל גם את שם הרוח.
     * @param ghostName  שם ספריית התמונות של הרוח (לדוגמה, "blinky" או "clyde").
     * @param frameDelay מספר ה"תיקתוקים" להמתנה בין פריימים.
     */
    public AnimationManager(String ghostName, int frameDelay) {
        this.ghostName = ghostName; // ודא שהשורה הזו קיימת
        this.frameDelay = frameDelay;
        loadFrames();
    }

    private void loadFrames() {
        try {
            frames = new BufferedImage[4][3];
            String[] directions = {"down", "right", "left", "up"};
            
            // --- זו השורה הקריטית ביותר ---
            // ודא שהנתיב נבנה עם המשתנה ghostName ולא עם שם קבוע
            String basePath = "/resources/ghosts/" + this.ghostName + "/";

            for (int i = 0; i < directions.length; i++) {
                // ודא שגם שמות הקבצים משתמשים ב-ghostName
                BufferedImage frame1 = ImageIO.read(getClass().getResourceAsStream(basePath + this.ghostName + "3" + directions[i] + ".png"));
                BufferedImage frame2 = ImageIO.read(getClass().getResourceAsStream(basePath + this.ghostName + "4" + directions[i] + ".png"));
                frames[i][0] = frame1;
                frames[i][1] = frame2;
                frames[i][2] = frame1;
            }
        } catch (Exception e) {
            // הוספת הדפסת שגיאה מפורטת שתעזור לנו
            System.err.println("!!! ERROR: Failed to load animation frames for ghost: " + this.ghostName);
            e.printStackTrace();
        }
    }

    // שאר הקובץ (update, getCurrentFrame) נשאר ללא שינוי
    public void update(boolean isMoving) {
        if (isMoving) {
            frameTick++;
            if (frameTick >= frameDelay) {
                frameIndex = (frameIndex + 1) % 3;
                frameTick = 0;
            }
        } else {
            frameIndex = 0;
            frameTick = 0;
        }
    }

    public BufferedImage getCurrentFrame(int direction) {
        if (frames != null && direction >= 0 && direction < frames.length) {
            return frames[direction][frameIndex];
        }
        return null;
    }
}