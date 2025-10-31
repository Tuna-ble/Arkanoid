package org.example.gamelogic.core;

import org.example.config.GameConstants;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.events.LifeLostEvent;
import org.example.gamelogic.states.GameStateEnum;

public final class LifeManager {
    private static class SingletonHolder {
        private static final LifeManager INSTANCE = new LifeManager();
    }

    public static LifeManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private int lives;

    private LifeManager() {
        this.lives = GameConstants.INITIAL_LIVES;
    }

    public void loseLife() {
        if (this.lives == 0) {
            return;
        }
        this.lives--;

        if (lives > 0) {
            EventManager.getInstance().publish(new LifeLostEvent(lives));
        } else {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.GAME_OVER)
            );
        }
    }

    public void addLife() {
        this.lives++;
    }

    public void reset() {
        this.lives = GameConstants.INITIAL_LIVES;
    }

    public int getLives() {
        return this.lives;
    }
}
