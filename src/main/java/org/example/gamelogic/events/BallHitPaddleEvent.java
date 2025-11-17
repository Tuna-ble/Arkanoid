package org.example.gamelogic.events;

import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;

public final class BallHitPaddleEvent extends GameEvent {
    private final IBall ball;
    private final Paddle paddle;

    /**
     * Tạo event khi bóng chạm vào paddle.
     *
     * @param ball   bóng gây ra va chạm
     * @param paddle paddle bị bóng đập trúng
     */
    public BallHitPaddleEvent(IBall ball, Paddle paddle) {
        this.ball = ball;
        this.paddle = paddle;
    }
    /**
     * @return bóng va chạm với paddle
     */
    public IBall getBall() {
        return ball;
    }

    /**
     * @return paddle bị bóng chạm vào
     */
    public Paddle getPaddle() {
        return paddle;
    }
}
