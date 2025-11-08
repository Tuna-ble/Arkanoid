package org.example.gamelogic.strategy.powerup;

import org.example.config.GameConstants;
import org.example.gamelogic.core.BallManager;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.states.PlayingState;

import java.util.ArrayList;
import java.util.List;

public class MultiBallStrategy implements PowerUpStrategy {
    private final int CLONES_PER_BALL = 2;
    private final double ANGLE_OFFSET = 15.0;

    @Override
    public void apply(PlayingState ps) {
        BallManager ballManager = ps.getBallManager();
        if (ballManager == null) return;

        // tránh lỗi ConcurrentModificationException
        List<IBall> newBalls = new ArrayList<>();
        List<IBall> currentBalls = new ArrayList<>(ballManager.getActiveBalls());

        int currentBallCount = currentBalls.size();

        for (IBall originalBall : currentBalls) {
            if (originalBall.isAttachedToPaddle()) continue;

            for (int i = 0; i < CLONES_PER_BALL; i++) {
                if (currentBallCount >= GameConstants.MAX_BALL_COUNT) {
                    break;
                }

                IBall clone = originalBall.clone(); // clone() đã set attachedToPaddle=false
                clone.setPosition(originalBall.getX(), originalBall.getY());

                double currentAngle = Math.atan2(originalBall.getDy(), originalBall.getDx());
                double newAngle;
                if (i == 0) {
                    newAngle = currentAngle + Math.toRadians(ANGLE_OFFSET); // Lệch 1 hướng
                } else {
                    newAngle = currentAngle - Math.toRadians(ANGLE_OFFSET); // Lệch hướng còn lại
                }

                double speed = originalBall.getSpeed();
                clone.setDx(speed * Math.cos(newAngle));
                clone.setDy(speed * Math.sin(newAngle));

                newBalls.add(clone);
                currentBallCount++;
            }
            if (currentBallCount >= GameConstants.MAX_BALL_COUNT) {
                break;
            }
        }

        for (IBall ball : newBalls) {
            ballManager.addBall(ball);
        }
    }

    @Override
    public void update(PlayingState playingState, double deltatime) {

    }

    @Override
    public void remove(PlayingState playingState) {

    }

    @Override
    public boolean isExpired() {
        return true;
    }

    @Override
    public void reset() {

    }

    @Override
    public PowerUpStrategy clone() {
        return new MultiBallStrategy();
    }
}
