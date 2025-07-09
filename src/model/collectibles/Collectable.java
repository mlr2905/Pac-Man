package collectibles;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import controller.managers.ScoreManager;

public interface Collectable {
    void update();
    void draw(Graphics2D g2);
    boolean isCollected();
    void setCollected(boolean collected);
    Rectangle getSolidArea();
    void onCollected(ScoreManager sm);
}