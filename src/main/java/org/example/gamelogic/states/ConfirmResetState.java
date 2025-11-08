package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.HighscoreManager;
import org.example.gamelogic.core.ProgressManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class ConfirmResetState implements GameState {

    private final Button yesButton;
    private final Button noButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private final Font titleFont = new Font("Arial", 40);
    private final DropShadow titleShadow = new DropShadow(10, Color.color(0, 0, 0, 0.7));
    private final LinearGradient titleFill = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#ff8888")),
            new Stop(1, Color.web("#ff4444"))
    );

    public ConfirmResetState() {
        double buttonY = 350;
        double buttonSpacing = 40;

        this.yesButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                buttonY,
                "YES (Reset All Data)"
        );

        this.noButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                buttonY + GameConstants.UI_BUTTON_HEIGHT + buttonSpacing,
                "NO (Go Back)"
        );
    }

    @Override
    public void update(double deltaTime) {
    }

    private void updateButtons(I_InputProvider inputProvider) {
        yesButton.update(inputProvider);
        noButton.update(inputProvider);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc,
                "ARE YOU SURE?",
                centerX,
                150,
                titleFont,
                titleFill,
                Color.color(0,0,0,0.9),
                2.0,
                titleShadow
        );
        TextRenderer.drawOutlinedText(
                gc,
                "This will delete ALL highscores",
                centerX,
                220,
                new Font("Arial", 24), Color.WHITE, Color.BLACK, 1.0, null
        );
        TextRenderer.drawOutlinedText(
                gc,
                "and reset ALL level progress.",
                centerX,
                260,
                new Font("Arial", 24), Color.WHITE, Color.BLACK, 1.0, null
        );

        yesButton.render(gc);
        noButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        updateButtons(inputProvider);

        if (yesButton.isClicked()) {
            HighscoreManager.resetHighscores();
            ProgressManager.resetProgress();
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        } else if (noButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }
    }
}