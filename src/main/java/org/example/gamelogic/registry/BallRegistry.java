package org.example.gamelogic.registry;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.Ball;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BallRegistry {
    private static final List<Ball> balls = new ArrayList<>();

    public static void register(Ball ball) {
        balls.add(ball);
    }

    public static void unregister(Ball ball) {
        balls.remove(ball);
    }

    public static List<Ball> all() {
        return balls;
    }

    public static void clear() {
        balls.clear();
    }

    public static void updateAll(double deltaTime) {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball b = iterator.next();
            b.update(deltaTime);
        }
    }

    public static void renderAll(GraphicsContext gc) {
        for (Ball b : balls) {
            b.render(gc);
        }
    }
}
