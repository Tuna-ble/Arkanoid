package org.example.gamelogic.factory;

import org.example.gamelogic.entities.Ball;

public class BallFactory {
    public static Ball createBall(int x, int y, int width, int height, int dx, int dy) {
        return new Ball(x, y, width, height, dx, dy);
    }
}
