package org.example.gamelogic.events;

import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.bricks.Brick;

public final class BrickDamagedEvent extends GameEvent {
    private final Brick damagedBrick;
    private final GameObject damageSource;

    /**
     * Tạo event khi một viên gạch bị gây sát thương.
     *
     * @param brick gạch bị damage
     * @param obj   đối tượng gây ra sát thương (ball, laser, enemy...)
     */
    public BrickDamagedEvent(Brick brick, GameObject obj) {
        this.damagedBrick = brick;
        this.damageSource = obj;
    }

    /**
     * @return gạch vừa bị damage
     */
    public Brick getDamagedBrick() {
        return damagedBrick;
    }

    /**
     * @return đối tượng gây ra sát thương
     */
    public GameObject getDamageSource() {
        return damageSource;
    }
}
