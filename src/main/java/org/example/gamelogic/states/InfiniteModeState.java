package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ProgressManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;

import java.util.Map;


public final class InfiniteModeState implements GameState {

    private Map<String, String> data = ProgressManager.loadSession(GameModeEnum.INFINITE.toString());
    private int currentWave = data.isEmpty() ? -1 : Integer.parseInt(data.get("level"));

    private final Button newGameButton;
    private final Button continueButton;
    private final Button backButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    public InfiniteModeState() {
        this.newGameButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2,
                "New Game"
        );
        this.continueButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2 + 100,
                GameConstants.UI_BUTTON_WIDTH,
                GameConstants.UI_BUTTON_HEIGHT * (currentWave > 1 ? 1.2 : 1),
                "Continue" + (currentWave > 1 ? "\nWave " + Integer.toString(currentWave) : "")
        );
        this.backButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2 +
                        GameConstants.UI_BUTTON_HEIGHT * (currentWave > 1 ? 1.2 : 1) + 140,
                "Back"
        );
    }

    @Override
    public void update(double deltaTime) {

    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (newGameButton != null) {
            newGameButton.update(inputProvider);
        }
        if (continueButton != null) {
            continueButton.update(inputProvider);
        }
        if (backButton != null) {
            backButton.update(inputProvider);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        if (newGameButton != null) {
            newGameButton.render(gc);
        }
        if (continueButton != null) {
            if (currentWave > 1) {
                continueButton.render(gc);
            } else {
                gc.save();
                gc.setGlobalAlpha(0.5);
                continueButton.render(gc);
                gc.restore();
            }
        }
        if (backButton != null) {
            backButton.render(gc);
        }
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        updateButtons(inputProvider);
        if (newGameButton != null && newGameButton.isClicked()) {
            ProgressManager.clearSession(GameModeEnum.INFINITE.toString());
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PLAYING)
            );
        }
        if (continueButton != null && continueButton.isClicked() && currentWave > 1) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PLAYING)
            );
        }
        if (backButton != null && backButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.GAME_MODE)
            );
        }
    }
}
