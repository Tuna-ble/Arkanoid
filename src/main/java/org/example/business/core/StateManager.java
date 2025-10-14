package org.example.business.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.business.states.GameState;
import org.example.business.states.*;

import java.util.HashMap;
import java.util.Map;

public class StateManager {

    private final Map<String, GameState> states;
    private GameState currentState;

    public StateManager() {
        states = new HashMap<>();
        states.put("MENU", new MainMenuState(this));
        states.put("PLAYING", new PlayingState(this));
        states.put("PAUSE", new PauseState(this));
        states.put("GAME_OVER", new GameOverState(this));

        currentState = states.get("MENU");
    }

    public void setState(String name) {
        GameState newState = states.get(name);
        if (newState != null) {

            if ("GAME_OVER".equals(name) && currentState instanceof PlayingState) {
                PlayingState playingState = (PlayingState) currentState;
                GameOverState gameOverState = (GameOverState) newState;
                gameOverState.setFinalScore(playingState.getScore());
            }

            if ("PLAYING".equals(name) && currentState instanceof MainMenuState) {
                ((PlayingState) newState).initLevel();
            }
            if ("PLAYING".equals(name) && currentState instanceof GameOverState) {
                ((PlayingState) newState).initLevel();
            }

            currentState = newState;
        } else {
            System.err.println("Lỗi: State '" + name + "' không tồn tại!");
        }
    }


    public void update() {
        if (currentState != null)
            currentState.update();
    }

    public void render(GraphicsContext gc) {
        if (currentState != null)
            currentState.render(gc);
    }

    public GameState getState(String name) {
        return states.get(name);
    }
}
