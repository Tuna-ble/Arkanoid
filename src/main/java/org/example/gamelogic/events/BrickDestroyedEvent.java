package org.example.gamelogic.events;

import org.example.gamelogic.entities.bricks.Brick;

public final class BrickDestroyedEvent extends GameEvent {
    private final Brick destroyedBrick;

    /**
     * Tạo event khi một viên gạch bị phá hủy.
     *
     * @param brick gạch đã bị phá
     */
    public BrickDestroyedEvent(Brick brick) {
        this.destroyedBrick = brick;
    }

    /**
     * @return gạch vừa bị phá hủy
     */
    public Brick getHitBrick() {
        return destroyedBrick;
    }
}
