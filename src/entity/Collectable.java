// New: Collectable.java interface
package entity;

import java.awt.Rectangle;

public interface Collectable {
    boolean isCollected();
    void setCollected(boolean collected);
    Rectangle getSolidArea();
    void onCollected(ScoreManager sm); // Method to handle score addition etc.
    void draw(java.awt.Graphics2D g2); // If you want a common draw method
    void update(); // For update logic, like blinking for PowerPellet
}