package org.example.gamelogic.events;

public final class LifeLostEvent extends GameEvent {
    private final int remainingLives;

    public LifeLostEvent(int remainingLives) {
        this.remainingLives = remainingLives;
    }

    public int getRemainingLives() {
        return remainingLives;
    }
}
