package entity.ghost;

class InkyConfig {
    // מיקום התחלה בתוך בית הרוחות
    public final int START_IN_EIGHT_X = 20;
    public final int START_IN_EIGHT_Y = 7;

    // אותה נקודת יציאה כמו שאר הרוחות
    public final int EXIT_TILE_X = 18;
    public final int EXIT_TILE_Y = 6;
    public final int TILE_ABOVE_EXIT_X = 18;
    public final int TILE_ABOVE_EXIT_Y = 5;

    // נקודת מטרה למצב "פיזור": הפינה הימנית התחתונה
    public final int SCATTER_TARGET_X = 26; // בהנחת רוחב מפה של כ-28 משבצות
    public final int SCATTER_TARGET_Y = 13;
}