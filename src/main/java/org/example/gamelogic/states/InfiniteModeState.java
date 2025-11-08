package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import javafx.scene.input.KeyCode;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.enemy.Enemy;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;


public final class InfiniteModeState implements GameState {

    private final Button playButton;
    private final Button backButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    public InfiniteModeState() {
        this.playButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2,
                "Play"
        );
        this.backButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2 + playButton.getWidth(),
                "Back"
        );
    }

    @Override
    public void update(double deltaTime) {

    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (playButton != null) {
            playButton.update(inputProvider);
        }
        if (backButton != null) {
            backButton.update(inputProvider);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        if (playButton != null) {
            playButton.render(gc);
        }
        if (backButton != null) {
            backButton.render(gc);
        }
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        updateButtons(inputProvider);
        if (playButton != null && playButton.isClicked()) {
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
