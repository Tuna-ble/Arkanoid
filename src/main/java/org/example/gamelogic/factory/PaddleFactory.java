package org.example.gamelogic.factory;

import org.example.data.GameConstants;
import org.example.gamelogic.entities.Paddle;

public class PaddleFactory {
    public static Paddle createBall(int x, int y, int width, int height, int dx, int dy) {
        return new Paddle(x, y, width, height, dx, dy);
    }
}
