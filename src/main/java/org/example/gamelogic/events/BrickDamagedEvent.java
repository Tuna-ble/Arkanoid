package org.example.gamelogic.events;

import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.entities.bricks.Brick;

public final class BrickDamagedEvent extends GameEvent {
    private final Brick damagedBrick;
    private final GameObject damageSource;

    public BrickDamagedEvent(Brick brick, GameObject obj) {
        this.damagedBrick=brick;
        this.damageSource=obj;
    }

    public Brick getDamagedBrick() {
        return damagedBrick;
    }

    public GameObject getDamageSource() {
        return damageSource;
    }
}
