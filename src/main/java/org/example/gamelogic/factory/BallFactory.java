package org.example.gamelogic.factory;

import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.registry.BallRegistry;

public class BallFactory {
    private final BallRegistry registry;

    public BallFactory(BallRegistry registry) {
        this.registry = registry;
    }

    public IBall createBall(String ballType, double x, double y) {
        IBall prototype = registry.getPrototype(ballType);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found" + ballType);
        }
        IBall newBall = prototype.clone();
        newBall.setPosition(x,y);
        return newBall;
    }
}