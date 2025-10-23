package org.example.gamelogic.events;

import org.example.gamelogic.entities.Ball;

public final class BallHitWall extends GameEvent {
    private final Ball ball;

    public BallHitWall(Ball ball) {
        this.ball = ball;
    }

    public Ball getBall() {
        return ball;
    }
}
