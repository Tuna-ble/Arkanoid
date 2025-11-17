package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.bricks.Brick;

public final class BallHitBrickEvent extends GameEvent {
    private final Brick brick;
    private final IBall ball;

    /**
     * Tạo event khi bóng va chạm với gạch.
     *
     * @param brick gạch bị bóng đập trúng
     * @param ball  bóng gây ra va chạm
     */
    public BallHitBrickEvent(Brick brick, IBall ball) {
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
