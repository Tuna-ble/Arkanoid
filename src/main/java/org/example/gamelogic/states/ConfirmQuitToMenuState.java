package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class ConfirmQuitToMenuState implements GameState {
    private final GameState previousState;
    private final Font warningFont = new Font("Arial", 48);
    private final Font messageFont = new Font("Arial", 28);

    // Button layout uses GameConstants

    // Center screen position
    private double centerX, centerY;

    // Button instances
    private Button yesButton;
    private Button noButton;

    public ConfirmQuitToMenuState(GameState previousState) {
        this.previousState = previousState;
    }

    @Override
    public void update(double deltaTime) {
        // No physics or updates while paused
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.setTextAlign(TextAlignment.LEFT);
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();

        centerX = width / 2;
        centerY = height / 2;

        // Render previous frame (frozen gameplay)
        previousState.render(gc);

        // Semi-transparent dark overlay
        gc.setFill(new Color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, width, height);

        // Draw pause panel (card)
        double panelWidth = 500;
        double panelHeight = 310;
        double panelX = centerX - panelWidth / 2;
        double panelY = centerY - panelHeight / 2;

        gc.setFill(Color.web("#222"));
        gc.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // Title text (centered, outlined, shadow)
        gc.setTextAlign(TextAlignment.CENTER);
        DropShadow titleShadow = new DropShadow(10, Color.color(0, 0, 0, 0.7));
        TextRenderer.drawOutlinedText(
                gc,
                "WARNING",
                centerX,
                panelY + 60,
                warningFont,
                Color.WHITE,
                Color.color(0, 0, 0, 0.9),
                2.0,
                titleShadow
        );
        TextRenderer.drawOutlinedText(
                gc,
                "Your progress on this level\nwill not be saved.",
                centerX,
                panelY + 108,
                messageFont,
                Color.WHITE,
                Color.color(0, 0, 0, 0.9),
                2.0,
                titleShadow
        );
        TextRenderer.drawOutlinedText(
                gc,
                "Continue?",
                centerX,
                panelY + 180,
                messageFont,
                Color.YELLOW,
                Color.color(0, 0, 0, 0.9),
                2.0,
                titleShadow
        );
        // Calculate button positions
        double yesX = centerX - GameConstants.UI_BUTTON_WIDTH - GameConstants.UI_BUTTON_SPACING;
        double noX = centerX + GameConstants.UI_BUTTON_SPACING;
        double buttonY = panelY + 210;

        // Initialize buttons if not already created
        if (yesButton == null) {
            yesButton = new Button(yesX, buttonY, "Yes");
            yesButton.setFont(messageFont);
            yesButton.setColors(
                    Color.web("#444"),
                    Color.web("#555"),
                    Color.WHITE,
                    Color.WHITE,
                    Color.WHITE
            );
        } else {
            yesButton.setX(yesX);
            yesButton.setY(buttonY);
        }

        if (noButton == null) {
            noButton = new Button(noX, buttonY, "No");
            noButton.setFont(messageFont);
            noButton.setColors(
                    Color.web("#444"),
                    Color.web("#555"),
                    Color.WHITE,
                    Color.WHITE,
                    Color.WHITE
            );
        } else {
            noButton.setX(noX);
            noButton.setY(buttonY);
        }

        // Render buttons
        if (yesButton != null) yesButton.render(gc);
        if (noButton != null) noButton.render(gc);
        gc.setTextAlign(TextAlignment.LEFT);
    }


    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        // Update buttons to check hover and click states
        if (yesButton != null) yesButton.update(inputProvider);
        if (noButton != null) noButton.update(inputProvider);

        // Handle button clicks
        if (yesButton != null && yesButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        } else if (noButton != null && noButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PAUSED)
            );
        }
    }

    public GameState getPreviousState() {
        return previousState;
    }

    public void cleanUp() {
        if (previousState instanceof PlayingState) {
            ((PlayingState) previousState).cleanUp();
        }
    }
}
