package entity.ghost;

// ממשק פונקציונלי המגדיר אסטרטגיה למציאת מטרה
public interface TargetingStrategy {
    /**
     * מחשב את אריח המטרה של הרוח.
     * @param ghost הרוח שמבצעת את החישוב.
     * @return מערך של שני מספרים שלמים: [targetCol, targetRow].
     */
    int[] getTargetTile(Ghost ghost);
}