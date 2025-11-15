package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.PopupTransitionStrategy;

public final class PauseState implements GameState {
    private final GameState previousState;
    private final Window window;
    private final Font titleFont = new Font("Arial", 48);
    private final Font buttonFont = new Font("Arial", 28);

    private final Image normalImage;
    private final Image hoveredImage;
    private final Image bannerImage;
    // Button layout uses GameConstants

    // Center screen position
    private double centerX, centerY;

    // Button instances
    private AbstractButton banner;
    private AbstractButton resumeButton;
    private AbstractButton settingsButton;
    private AbstractButton quitButton;

    public PauseState(GameState previousState) {
        this.previousState = previousState;

        ITransitionStrategy transition = new PopupTransitionStrategy();
        this.window = new Window(previousState, 300, 400, transition);

        AssetManager am = AssetManager.getInstance();
        this.normalImage = am.getImage("button");
        this.hoveredImage = am.getImage("hoveredButton");
        this.bannerImage = am.getImage("banner2");

        centerX = GameConstants.SCREEN_WIDTH / 2;
        centerY = GameConstants.SCREEN_HEIGHT / 2;
        double bannerX = centerX - GameConstants.UI_BANNER_WIDTH / 2;
        double buttonX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double bannerY = window.getY() + GameConstants.UI_BUTTON_PADDING;
        double resumeY = bannerY + GameConstants.UI_BANNER_HEIGHT + GameConstants.UI_BUTTON_PADDING + 20;
        double settingsY = resumeY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING;
        double quitY = settingsY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING;
        banner = new Button(bannerX, bannerY,
                GameConstants.UI_BANNER_WIDTH, GameConstants.UI_BANNER_HEIGHT, bannerImage, bannerImage, "PAUSED");
        resumeButton = new Button(buttonX, resumeY, normalImage, hoveredImage, "Resume");
        quitButton = new Button(buttonX, quitY, normalImage, hoveredImage, "Quit");
        settingsButton = new Button(buttonX, settingsY, normalImage, hoveredImage, "Settings");

        window.addButton(banner);
        window.addButton(resumeButton);
        window.addButton(settingsButton);
        window.addButton(quitButton);
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
        if (inputProvider == null) return;

        window.handleInput(inputProvider);

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
                    new ChangeStateEvent(GameStateEnum.CONFIRM_QUIT_TO_MENU)
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
