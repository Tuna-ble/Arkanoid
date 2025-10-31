package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;
import javafx.scene.text.TextAlignment;
import javafx.scene.effect.DropShadow;
import org.example.config.GameConstants;
import org.example.gamelogic.core.GameManager;
import org.example.presentation.InputHandler;
import org.example.presentation.TextRenderer;

public final class PauseState implements GameState {
    private final GameManager gameManager;
    private final GameState previousState;
    private final Font titleFont = new Font("Arial", 48);
    private final Font buttonFont = new Font("Arial", 28);

    // Button layout uses GameConstants

    // Center screen position
    private double centerX, centerY;

    // Buttons’ positions
    private double resumeX, resumeY;
    private double quitX, quitY;

    public PauseState(GameManager gameManager, GameState previousState) {
        this.gameManager = gameManager;
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
        double panelWidth = 300;
        double panelHeight = 250;
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
                "PAUSED",
                centerX,
                panelY + 60,
                titleFont,
                Color.WHITE,
                Color.color(0,0,0,0.9),
                2.0,
                titleShadow
        );

        // Draw buttons
        resumeX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        resumeY = panelY + 100;
        quitX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        quitY = resumeY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING;

        renderButton(gc, "Resume", resumeX, resumeY);
        renderButton(gc, "Quit", quitX, quitY);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void renderButton(GraphicsContext gc, String text, double x, double y) {
        gc.setFill(Color.web("#444"));
        gc.fillRoundRect(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT, 10, 10);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT, 10, 10);

        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc,
                text,
                x + (GameConstants.UI_BUTTON_WIDTH / 2),
                y + GameConstants.UI_BUTTON_HEIGHT / 2 + 8,
                buttonFont,
                Color.WHITE,
                Color.color(0,0,0,0.85),
                1.5,
                new DropShadow(6, Color.color(0,0,0,0.6))
        );
    }

    @Override
    public void handleInput(InputHandler input) {
        if (input.isMouseClicked()) {
            int mx = input.getMouseX();
            int my = input.getMouseY();

            if (mx >= resumeX && mx <= resumeX + GameConstants.UI_BUTTON_WIDTH &&
                    my >= resumeY && my <= resumeY + GameConstants.UI_BUTTON_HEIGHT) {
                gameManager.setState(previousState);
            }

            if (mx >= quitX && mx <= quitX + GameConstants.UI_BUTTON_WIDTH &&
                    my >= quitY && my <= quitY + GameConstants.UI_BUTTON_HEIGHT) {
                gameManager.setState(new MainMenuState(gameManager));
            }
        }
    }
}
