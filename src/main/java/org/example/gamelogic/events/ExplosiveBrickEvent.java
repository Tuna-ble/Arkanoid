package org.example.gamelogic.events;

import org.example.gamelogic.entities.bricks.Brick;

public final class ExplosiveBrickEvent extends GameEvent {
    private final Brick explosiveBrick;

    /**
     * Tạo event khi một explosive brick phát nổ.
     *
     * @param explosiveBrick viên gạch nổ gây sự kiện
     */
    public ExplosiveBrickEvent(Brick explosiveBrick) {
        this.explosiveBrick = explosiveBrick;
    }

    /**
     * @return gạch nổ kích hoạt event
     */
    public Brick getExplosiveBrick() {
        return explosiveBrick;
    }
}
