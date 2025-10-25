package org.example.gamelogic.events;

import org.example.gamelogic.entities.bricks.Brick;

public final class BrickDestroyedEvent extends GameEvent {
    private final Brick destroyedBrick;

    public BrickDestroyedEvent(Brick brick) {
        this.destroyedBrick = brick;
    }

    public Brick getHitBrick() {
        return destroyedBrick;
    }
}
