package entity.ghost;

class ClydeConfig {
    // מיקום התחלה בתוך בית הרוחות
    public final int START_IN_EIGHT_X = 16;
    public final int START_IN_EIGHT_Y = 7;

    // אותה נקודת יציאה כמו בלינקי
    public final int EXIT_TILE_X = 18;
    public final int EXIT_TILE_Y = 6;
    public final int TILE_ABOVE_EXIT_X = 18;
    public final int TILE_ABOVE_EXIT_Y = 5;

    // נקודת מטרה למצב "פיזור" (כאשר הוא לא רודף אחרי פקמן)
    // בדרך כלל זו תהיה פינה במבוך, למשל הפינה השמאלית התחתונה.
    public final int SCATTER_TARGET_X = 1;
    public final int SCATTER_TARGET_Y = 13;
}