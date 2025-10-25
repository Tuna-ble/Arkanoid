package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;

public final class BallHitWall extends GameEvent {
    private final IBall ball;

    public BallHitWall(IBall ball) {
        this.ball = ball;
    }

    public IBall getBall() {
        return ball;
    }
}
