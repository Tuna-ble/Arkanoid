package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.PopupTransitionStrategy;

public final class ConfirmQuitToMenuState implements GameState {
    private final GameState previousState;
    private final Window window;
    private final Font warningFont;
    private final Font messageFont;

    // Button layout uses GameConstants
    private Image buttonImage;
    private Image hoveredImage;

    // Center screen position
    private double centerX = GameConstants.SCREEN_WIDTH / 2;
    private double centerY = GameConstants.SCREEN_HEIGHT / 2;

    // Button instances
    private AbstractButton yesButton;
    private AbstractButton noButton;

    public ConfirmQuitToMenuState(GameState previousState) {
        this.previousState = previousState;

        AssetManager am = AssetManager.getInstance();
        warningFont = am.getFont("Anxel", 45);
        messageFont = am.getFont("Anxel", 30);

        ITransitionStrategy transition = new PopupTransitionStrategy();
        this.window = new Window(previousState, 500, 400, transition);

        buttonImage = am.getImage("selectButton");
        hoveredImage = am.getImage("selectButtonHovered");

        double buttonX = window.getX() + window.getWidth() / 2 - GameConstants.UI_BUTTON_WIDTH / 2;
        double buttonY = window.getY() + 210;

        yesButton = new Button(buttonX, buttonY, buttonImage, hoveredImage, "Yes");
        yesButton.setTransition(new WipeElementTransitionStrategy(0.5));

        noButton = new Button(buttonX, buttonY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_PADDING,
                buttonImage, hoveredImage, "No");
        noButton.setTransition(new WipeElementTransitionStrategy(0.5));

        window.addButton(yesButton);
        window.addButton(noButton);
    }

    @Override
    public void update(double deltaTime) {
        // No physics or updates while paused
        window.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        window.render(gc);

        if (window.transitionFinished()) {
            // Title text (centered, outlined, shadow)
            gc.setTextAlign(TextAlignment.CENTER);
            DropShadow titleShadow = new DropShadow(10, Color.color(0, 0, 0, 0.7));
            TextRenderer.drawOutlinedText(
                    gc,
                    "WARNING",
                    centerX,
                    window.getY() + 60,
                    warningFont,
                    Color.RED,
                    Color.color(0, 0, 0, 0.9),
                    2.0,
                    titleShadow
            );
            TextRenderer.drawOutlinedText(
                    gc,
                    "Your progress on this level\nwill not be saved.",
                    centerX,
                    window.getY() + 98,
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
                    window.getY() + 180,
                    messageFont,
                    Color.YELLOW,
                    Color.color(0, 0, 0, 0.9),
                    2.0,
                    titleShadow
            );
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }


    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        window.handleInput(inputProvider);

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
