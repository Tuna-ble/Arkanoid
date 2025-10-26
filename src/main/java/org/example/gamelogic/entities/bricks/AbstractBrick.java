package org.example.gamelogic.entities.bricks;

import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.GameObject;
import org.example.gamelogic.events.BrickHitEvent;

public abstract class AbstractBrick extends GameObject implements Brick {
    public AbstractBrick(double x, double y, double width, double height) {
        super(x, y, width, height);
        subscribeToHitEvent();
    }

    private void subscribeToHitEvent() {
        EventManager.getInstance().subscribe(
                BrickHitEvent.class,
                this::onHit
        );
    }

    private void onHit(BrickHitEvent event) {
        if (event.getBrick() == this && !isDestroyed()) {
            takeDamage();
        }
    }

    @Override
    public boolean isDestroyed() {
        return !this.isAlive();
    }

    @Override
    public abstract Brick clone();

    @Override
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public GameObject getGameObject() {
        return this;
    }
}