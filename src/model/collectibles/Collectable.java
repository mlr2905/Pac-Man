// New: Collectable.java interface
package collectibles;

import java.awt.Rectangle;

import managers.ScoreManager;

public interface Collectable {
    boolean isCollected();
    void setCollected(boolean collected);
    Rectangle getSolidArea();
    void onCollected(ScoreManager sm); // Method to handle score addition etc.
    void draw(java.awt.Graphics2D g2); // If you want a common draw method
    void update(); // For update logic, like blinking for PowerPellet
}