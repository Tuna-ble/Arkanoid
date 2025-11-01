package org.example.gamelogic.events;

import org.example.gamelogic.entities.bricks.Brick;

public final class BrickExplodedEvent extends GameEvent {
    private final Brick explosiveBrick;

    public BrickExplodedEvent(Brick explosiveBrick) {
        this.explosiveBrick = explosiveBrick;
    }

    public Brick getExplosiveBrick() {
        return explosiveBrick;
    }
}
