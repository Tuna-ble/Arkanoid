package org.example.gamelogic.entities.bricks;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.Collidable;
import org.example.gamelogic.entities.GameObject;

public interface Brick extends Collidable {
    void takeDamage(double damage);
    boolean isBreakable();
    void setPosition(double x, double y);
    Brick clone();

    boolean withinRangeOf(Brick other);

    void update(double deltaTime);
    void render(GraphicsContext gc);

    int getId();

    int getHealth();

    void setHealth(int health);

    void setId(int brickId);

    void setDestroyed(boolean b);
}
