package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;

public final class BallHitPaddleEvent extends GameEvent {
    private final IBall ball;
    private final Paddle paddle;

    public BallHitPaddleEvent(IBall ball, Paddle paddle) {
        this.ball = ball;
        this.paddle = paddle;
    }

    public IBall getBall() {
        return ball;
    }
    public Paddle getPaddle() {
        return paddle;
    }
}
