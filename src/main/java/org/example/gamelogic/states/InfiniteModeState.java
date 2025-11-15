package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ProgressManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;

import java.util.Map;


public final class InfiniteModeState implements GameState {

    private Map<String, String> data = ProgressManager.loadSession(GameModeEnum.INFINITE.toString());
    private int currentWave = data.isEmpty() ? -1 : Integer.parseInt(data.get("level"));

    private final Button newGameButton;
    private final Button continueButton;
    private final Button backButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private final Font titleFont = new Font("Arial", 70);
    private final DropShadow titleShadow = new DropShadow(14, Color.color(0, 0, 0, 0.7));
    private final LinearGradient titleFill = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#88ccff")),
            new Stop(1, Color.web("#4488ff"))
    );

    public InfiniteModeState() {
        AssetManager am = AssetManager.getInstance();
        final Image buttonImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");

        this.newGameButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2,
                buttonImage,
                hoveredImage,
                "New Game"
        );
        this.continueButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2 + 100,
                GameConstants.UI_BUTTON_WIDTH,
                GameConstants.UI_BUTTON_HEIGHT * (currentWave > 1 ? 1.2 : 1),
                buttonImage,
                hoveredImage,
                "Continue" + (currentWave > 1 ? "\nWave " + Integer.toString(currentWave) : "")
        );
        this.backButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT / 2 +
                        GameConstants.UI_BUTTON_HEIGHT * (currentWave > 1 ? 1.2 : 1) + 140,
                buttonImage,
                hoveredImage,
                "Back"
        );
    }

    @Override
    public void update(double deltaTime) {

    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (newGameButton != null) {
            newGameButton.handleInput(inputProvider);
        }
        if (continueButton != null) {
            continueButton.handleInput(inputProvider);
        }
        if (backButton != null) {
            backButton.handleInput(inputProvider);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc,
                "INFINITE MODE",
                centerX,
                110,
                titleFont,
                titleFill,
                Color.color(0,0,0,0.9),
                3.0,
                titleShadow
        );

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
