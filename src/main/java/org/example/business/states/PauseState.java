package org.example.business.states;

import org.example.business.core.StateManager;
import org.example.presentation.controller.InputHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class PauseState implements GameState {
    private final String[] options = {"Resume", "Main Menu"};
    private final StateManager stateManager;
    private int currentOption = 0;

    private static final int SCREEN_WIDTH = 960;
    private static final int SCREEN_HEIGHT = 600;

    public PauseState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void update() {
        handleInput();
    }

    @Override
    public void render(GraphicsContext gc) {

        GameState playingState = stateManager.getState("PLAYING");
        if (playingState != null) {
            playingState.render(gc);
        }

        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        gc.setFont(Font.font("Impact", FontWeight.BOLD, 80));
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("PAUSED", SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 3.0);

        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        for (int i = 0; i < options.length; i++) {
            if (i == currentOption) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.WHITE);
            }
            gc.fillText(options[i], SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0 + i * 70);
        }
    }

    @Override
    public void handleInput() {
        if (InputHandler.isKeyJustPressed(KeyCode.ESCAPE)) {
            stateManager.setState("PLAYING");
            return;
        }

        if (InputHandler.isKeyJustPressed(KeyCode.DOWN)) {
            currentOption++;
            if (currentOption >= options.length) {
                currentOption = 0;
            }
        }

        if (InputHandler.isKeyJustPressed(KeyCode.UP)) {
            currentOption--;
            if (currentOption < 0) {
                currentOption = options.length - 1;
            }
        }

        if (InputHandler.isKeyJustPressed(KeyCode.ENTER)) {
            selectOption();
        }
    }

    private void selectOption() {
        switch (currentOption) {
            case 0:
                stateManager.setState("PLAYING");
                break;
            case 1:
                stateManager.setState("MENU");
                break;
        }
    }
}