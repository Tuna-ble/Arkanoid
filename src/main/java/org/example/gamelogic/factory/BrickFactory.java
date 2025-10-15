package org.example.gamelogic.factory;

import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.registry.BrickRegistry;

public class BrickFactory {
    //dung factory + registry pattern
    private final BrickRegistry registry;

    public BrickFactory(BrickRegistry registry) {
        this.registry = registry;
    }

    //public Brick createBrick() {}
}
