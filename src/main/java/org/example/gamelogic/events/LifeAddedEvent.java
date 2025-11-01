package org.example.gamelogic.events;

public class LifeAddedEvent extends GameEvent {
    private final int remainingLives;

    public LifeAddedEvent(int remainingLives) {
        this.remainingLives = remainingLives;
    }

    public int getRemainingLives() {
        return remainingLives;
    }
}
