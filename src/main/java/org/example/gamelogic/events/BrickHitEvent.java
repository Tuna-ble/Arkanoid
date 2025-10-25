package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.bricks.Brick;

public final class BrickHitEvent extends GameEvent {
    private final Brick brick;
    private final IBall ball;

    public BrickHitEvent(Brick brick, IBall ball) {
        this.brick = brick;
        this.ball = ball;
    }

    public Brick getBrick() {
        return brick;
    }

    public IBall getBall() {
        return ball;
    }
}
