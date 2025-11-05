package org.example.gamelogic.strategy.movement;

import org.example.gamelogic.entities.enemy.Enemy;

import java.util.Random;

public class DashMovementStrategy implements EnemyMovementStrategy {
    private enum State {
        WAITING,
        DASHING
    }

    private State currentState = State.DASHING;
    private Random random = new Random();

    private double waitTimer = 0.0;
    private double dashTimer = 0.0;

    private final double WAIT_DURATION = 3.0;
    private final double DASH_DURATION = 0.4;
    private final double DASH_SPEED = 400.0;

    @Override
    public void move(Enemy enemy, double deltaTime) {
        switch (currentState) {
            case WAITING:
                waitTimer += deltaTime;

                if (waitTimer >= WAIT_DURATION) {
                    double angle = random.nextDouble() * 2 * Math.PI;

                    enemy.setDx(Math.cos(angle) * DASH_SPEED);
                    enemy.setDy(Math.sin(angle) * DASH_SPEED);

                    waitTimer = 0.0;
                    currentState = State.DASHING;
                }
                break;
            case DASHING:
                dashTimer += deltaTime;
                enemy.setX(enemy.getX() + enemy.getDx() * deltaTime);
                enemy.setY(enemy.getY() + enemy.getDy() * deltaTime);

                if (dashTimer >= DASH_DURATION) {
                    enemy.setDx(0);
                    enemy.setDy(0);

                    dashTimer = 0.0;
                    currentState = State.WAITING;
                }
                break;
        }
    }
}
