package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.data.SaveGameRepository;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.PopupTransitionStrategy;

public final class LogOutState implements GameState {

    private final GameState previousState;
    private final Window window;

    private final Font titleFont = new Font("Arial", 36);
    private final Font textFont = new Font("Arial", 22);

    private final Image normalImage;
    private final Image hoveredImage;

    private double centerX;

    private AbstractButton yesButton;
    private AbstractButton noButton;

    public LogOutState(GameState previousState) {
        this.previousState = previousState;

        ITransitionStrategy transition = new PopupTransitionStrategy();
        this.window = new Window(previousState, 300, 260,
                transition, "LOG OUT", null);

        AssetManager am = AssetManager.getInstance();
        this.normalImage = am.getImage("button");
        this.hoveredImage = am.getImage("hoveredButton");

        centerX = GameConstants.SCREEN_WIDTH / 2.0;
        double buttonX = centerX - GameConstants.UI_BUTTON_WIDTH / 2.0;

        double yesY = window.getY() + 100;
        double noY = yesY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING;

        yesButton = new Button(buttonX, yesY, normalImage, hoveredImage, "Yes");
        noButton = new Button(buttonX, noY, normalImage, hoveredImage, "No");

        window.addButton(yesButton);
        window.addButton(noButton);
    }

    @Override
    public void update(double deltaTime) {
        window.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.setTextAlign(TextAlignment.LEFT);
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        window.render(gc);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) {
            return;
        }

        window.handleInput(inputProvider);

        if (yesButton != null && yesButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.BEGIN)
            );

        } else if (noButton != null && noButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }
    }
}
