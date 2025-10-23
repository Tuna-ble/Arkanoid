package org.example.gamelogic.events;

public final class GameOverEvent extends GameEvent {
    private final int finalScore;

    public GameOverEvent(int finalScore) {
        this.finalScore = finalScore;
    }

    public int getFinalScore() {
        return finalScore;
    }
}
