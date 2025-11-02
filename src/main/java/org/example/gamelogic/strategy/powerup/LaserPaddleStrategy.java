package org.example.gamelogic.strategy.powerup;

import org.example.gamelogic.core.LaserManager;
import org.example.gamelogic.entities.BulletFrom;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.states.PlayingState;

public class LaserPaddleStrategy implements PowerUpStrategy {
    private double remainingTime = 5.0;
    private double timeSinceLastShot=1;
    private final double interval=0.5;

    @Override
    public void apply(PlayingState playingState) {

    }

    @Override
    public void update(PlayingState playingState, double deltatime) {
        remainingTime -= deltatime;
        timeSinceLastShot+=deltatime;

        if (timeSinceLastShot>=interval) {
            timeSinceLastShot=0;
            Paddle paddle = playingState.getPaddle();

            double leftX = paddle.getX() + 10;
            double rightX = paddle.getX() + paddle.getWidth() - 14;
            double y = paddle.getY() - 16;

            LaserManager.getInstance().createBullet(leftX, y, 600, BulletFrom.PLAYER);
            LaserManager.getInstance().createBullet(rightX, y, 600, BulletFrom.PLAYER);
        }

        if (remainingTime <= 0) {
            remove(playingState);
        }
    }

    @Override
    public void remove(PlayingState playingState) {

    }

    @Override
    public boolean isExpired() {
        return remainingTime <= 0;
    }

    @Override
    public void reset() {
        remainingTime = 5.0;
    }
}
