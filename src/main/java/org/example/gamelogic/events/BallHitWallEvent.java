package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;

public final class BallHitWallEvent extends GameEvent {
    private final IBall ball;

    public BallHitWallEvent(IBall ball) {
        this.ball = ball;
    }

    public IBall getBall() {
        return ball;
    }
}
