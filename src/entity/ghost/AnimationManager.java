package entity.ghost;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * מנהל את האנימציה של דמות במשחק.
 * אחראי על טעינת פריימים, עדכון הפריים הנוכחי,
 * ומספק את התמונה הנכונה לציור.
 */
public class AnimationManager {

    private BufferedImage[][] frames; // [כיוון][מספר פריים]
    private int frameIndex = 0;
    private int frameTick = 0;
    private final int frameDelay; // כמה פריימים של המשחק לחכות לפני החלפת תמונת אנימציה

    /**
     * @param frameDelay מספר ה"תיקתוקים" להמתנה בין פריימים.
     */
    public AnimationManager(int frameDelay) {
        this.frameDelay = frameDelay;
        loadFrames();
    }

    private void loadFrames() {
        try {
            frames = new BufferedImage[4][3]; // 4 כיוונים, 3 פריימים לאנימציית תנועה
            String[] directions = {"down", "right", "left", "up"};
            for (int i = 0; i < directions.length; i++) {
                // שם הקובץ צפוי להיות בפורמט: blinky3{direction}.png, blinky4{direction}.png
                BufferedImage frame1 = ImageIO.read(getClass().getResourceAsStream("/resources/ghosts/blinky/blinky3" + directions[i] + ".png"));
                BufferedImage frame2 = ImageIO.read(getClass().getResourceAsStream("/resources/ghosts/blinky/blinky4" + directions[i] + ".png"));
                frames[i][0] = frame1;
                frames[i][1] = frame2;
                frames[i][2] = frame1; // חזרה לפריים הראשון ליצירת לולאה חלקה
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * מעדכן את מונה הפריימים.
     * @param isMoving אם הדמות בתנועה, האנימציה מתקדמת.
     */
    public void update(boolean isMoving) {
        if (isMoving) {
            frameTick++;
            if (frameTick >= frameDelay) {
                frameIndex = (frameIndex + 1) % 3; // 3 הוא מספר הפריימים באנימציה
                frameTick = 0;
            }
        } else {
            frameIndex = 0; // במצב עמידה, הצג את הפריים הראשון
            frameTick = 0;
        }
    }

    /**
     * מחזיר את התמונה (פריים) הנוכחית בהתאם לכיוון.
     * @param direction כיוון התנועה (0:למטה, 1:ימינה, 2:שמאלה, 3:למעלה).
     * @return התמונה הנוכחית לציור.
     */
    public BufferedImage getCurrentFrame(int direction) {
        if (frames != null && direction >= 0 && direction < frames.length) {
            return frames[direction][frameIndex];
        }
        return null; // במקרה של שגיאה
    }
}