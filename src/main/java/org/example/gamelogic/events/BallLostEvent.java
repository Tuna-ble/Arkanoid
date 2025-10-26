package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;

public final class BallLostEvent extends GameEvent {
    private final IBall lostBall;

    public BallLostEvent(IBall ball) {
        this.lostBall = ball;
    }

    public IBall getLostBall() {
        return lostBall;
    }
}
