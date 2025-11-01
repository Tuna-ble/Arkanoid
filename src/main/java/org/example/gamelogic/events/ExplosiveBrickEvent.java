package org.example.gamelogic.events;

import org.example.gamelogic.entities.bricks.Brick;

public final class ExplosiveBrickEvent extends GameEvent {
    private final Brick explosiveBrick;

    public ExplosiveBrickEvent(Brick explosiveBrick) {
        this.explosiveBrick = explosiveBrick;
    }

    public Brick getExplosiveBrick() {
        return explosiveBrick;
    }
}
