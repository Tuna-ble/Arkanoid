package org.example.data;

import java.io.Serializable;
import java.time.LocalDate;

public class Score implements Comparable<Score>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String playerName;
    private final int scoreValue;
    private final LocalDate dateAchieved;

    public Score(String playerName, int scoreValue) {
        this.playerName = playerName;
        this.scoreValue = scoreValue;
        this.dateAchieved = LocalDate.now();
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public LocalDate getDateAchieved() {
        return dateAchieved;
    }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(other.scoreValue, this.scoreValue);
    }

    @Override
    public String toString() {
        return "Score{" +
                "player='" + playerName + '\'' +
                ", score=" + scoreValue +
                ", date=" + dateAchieved +
                '}';
    }
}