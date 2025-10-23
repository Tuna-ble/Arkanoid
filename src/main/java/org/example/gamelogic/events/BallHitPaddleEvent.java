package org.example.gamelogic.events;

import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.Paddle;

public final class BallHitPaddleEvent extends GameEvent {
    private final Ball ball;
    private final Paddle paddle;

    public BallHitPaddleEvent(Ball ball, Paddle paddle) {
        this.ball = ball;
        this.paddle = paddle;
    }

    public Ball getBall() {
        return ball;
    }
    public Paddle getPaddle() {
        return paddle;
    }
}
