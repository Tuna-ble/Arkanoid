package org.example.gamelogic.core;

public final class ScoreManager {
    private int currentScore;

    private static class SingletonHolder {
        private static final ScoreManager INSTANCE = new ScoreManager();
    }

    public static ScoreManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private ScoreManager() {
        this.currentScore = 0;
    }

    public void addScore(int score) {
        if (score > 0) {
            this.currentScore += score;
        }
    }
    public int getScore() {
        return this.currentScore;
    }

    public void resetScore() {
        this.currentScore = 0;
    }
}
