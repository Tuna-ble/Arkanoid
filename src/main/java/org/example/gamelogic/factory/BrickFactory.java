package org.example.gamelogic.factory;

import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.registry.BrickRegistry;

public class BrickFactory {
    private final BrickRegistry registry;

    public BrickFactory(BrickRegistry registry) {
        this.registry = registry;
    }

    public Brick createBrick(String brickType, double x, double y) {
        Brick prototype = registry.getPrototype(brickType);
        if (prototype == null) {
            throw new IllegalArgumentException("Prototype not found" + brickType);
        }
        Brick newBrick = prototype.clone();
        newBrick.setPosition(x,y);
        return newBrick;
    }
}
