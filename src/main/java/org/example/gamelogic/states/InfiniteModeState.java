package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;


public final class InfiniteModeState implements GameState {

    private final AbstractButton playButton;
    private final AbstractButton backButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    public InfiniteModeState() {
        AssetManager am = AssetManager.getInstance();
        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");
        this.playButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2,
                normalImage,
                hoveredImage,
                "Play"
        );
        this.backButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2 + playButton.getWidth(),
                normalImage,
                hoveredImage,
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
