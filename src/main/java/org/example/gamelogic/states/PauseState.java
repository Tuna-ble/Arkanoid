package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.text.TextAlignment;
import javafx.scene.effect.DropShadow;
import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class PauseState implements GameState {
    private final GameState previousState;
    private final Font titleFont = new Font("Arial", 48);
    private final Font buttonFont = new Font("Arial", 28);

    // Button layout uses GameConstants

    // Center screen position
    private double centerX, centerY;

    // Button instances
    private Button resumeButton;
    private Button settingsButton;
    private Button quitButton;

    public PauseState(GameState previousState) {
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

        // Render previous frame
        previousState.render(gc);

        // Semi-transparent dark overlay
        gc.setFill(new Color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, width, height);

        // Draw pause panel (card)
        double panelWidth = 300;
        double panelHeight = 350;
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

        // Calculate button positions
        double resumeX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double resumeY = panelY + 100;
        double settingsX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double settingsY = resumeY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING;
        double quitX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double quitY = settingsY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING;

        // Initialize buttons if not already created
        if (resumeButton == null) {
            resumeButton = new Button(resumeX, resumeY, "Resume");
            resumeButton.setFont(buttonFont);
            resumeButton.setColors(
                Color.web("#444"),
                Color.web("#555"),
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
            );
        } else {
            resumeButton.setX(resumeX);
            resumeButton.setY(resumeY);
        }
        
        if (quitButton == null) {
            quitButton = new Button(quitX, quitY, "Quit");
            quitButton.setFont(buttonFont);
            quitButton.setColors(
                Color.web("#444"),
                Color.web("#555"),
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
            );
        } else {
            quitButton.setX(quitX);
            quitButton.setY(quitY);
        }

        if (settingsButton == null) {
            settingsButton = new Button(settingsX, settingsY, "Settings");
            settingsButton.setFont(buttonFont);
            settingsButton.setColors(
                    Color.web("#444"),
                    Color.web("#555"),
                    Color.WHITE,
                    Color.WHITE,
                    Color.WHITE
            );
        } else {
            settingsButton.setX(settingsX);
            settingsButton.setY(settingsY);
        }

        // Render buttons
        if (resumeButton != null) resumeButton.render(gc);
        if (settingsButton != null) settingsButton.render(gc);
        if (quitButton != null) quitButton.render(gc);
        gc.setTextAlign(TextAlignment.LEFT);
    }


    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        
        // Update buttons to check hover and click states
        if (resumeButton != null) resumeButton.update(inputProvider);
        if (settingsButton != null) settingsButton.update(inputProvider);
        if (quitButton != null) quitButton.update(inputProvider);
        
        // Handle button clicks
        if (resumeButton != null && resumeButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.RESUME_GAME)
            );
        } else if (settingsButton != null && settingsButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.SETTINGS)
            );
        } else if (quitButton != null && quitButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }
    }

    public GameState getPreviousState() {
        return previousState;
    }
}
