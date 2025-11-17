package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;

public final class BallLostEvent extends GameEvent {
    private final IBall lostBall;

    /**
     * Tạo event khi một quả bóng bị mất (rơi khỏi màn chơi).
     *
     * @param ball bóng bị mất
     */
    public BallLostEvent(IBall ball) {
        this.lostBall = ball;
    }

    /**
     * @return bóng đã bị mất
     */
    public IBall getLostBall() {
        return lostBall;
    }
}
