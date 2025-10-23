package org.example.gamelogic.events;

import org.example.gamelogic.entities.Ball;

public final class BallLostEvent extends GameEvent {
    private final Ball lostBall;

    public BallLostEvent(Ball ball) {
        this.lostBall = ball;
    }

    public Ball getLostBall() {
        return lostBall;
    }
}
