package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;

public final class BallHitWallEvent extends GameEvent {
    private final IBall ball;

    /**
     * Tạo event khi bóng va vào tường.
     *
     * @param ball bóng gây ra va chạm
     */
    public BallHitWallEvent(IBall ball) {
        this.ball = ball;
    }

    /**
     * @return bóng vừa va vào tường
     */
    public IBall getBall() {
        return ball;
    }
}
