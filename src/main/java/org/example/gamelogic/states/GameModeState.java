package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.input.KeyCode;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class GameModeState implements GameState {

    private final AbstractButton infiniteButton;
    private final AbstractButton levelsButton;
    private final AbstractButton backButton;

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private final Font titleFont = new Font("Arial", 70);
    private final DropShadow titleShadow = new DropShadow(14, Color.color(0, 0, 0, 0.7));
    private final LinearGradient titleFill = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#88ccff")),
            new Stop(1, Color.web("#4488ff"))
    );

    public GameModeState() {
        double buttonY = 220;
        double buttonSpacing = 40;


        AssetManager am = AssetManager.getInstance();
        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");
        this.infiniteButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                buttonY,
                normalImage,
                hoveredImage,
                "INFINITE"
        );

        this.levelsButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                buttonY + GameConstants.UI_BUTTON_HEIGHT + buttonSpacing,
                normalImage,
                hoveredImage,
                "LEVELS"
        );

        this.backButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                buttonY + 2 * (GameConstants.UI_BUTTON_HEIGHT + buttonSpacing),
                normalImage,
                hoveredImage,
                "Back"
        );
    }

    @Override
    public void update(double deltaTime) { }

    private void updateButtons(I_InputProvider inputProvider) {
        infiniteButton.handleInput(inputProvider);
        levelsButton.handleInput(inputProvider);
         backButton.handleInput(inputProvider);
    }

    @Override
    public void render(GraphicsContext gc) {

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc,
                "GAME MODE",
                centerX,
                110,
                titleFont,
                titleFill,
                Color.color(0,0,0,0.9),
                3.0,
                titleShadow
        );

        infiniteButton.render(gc);
        levelsButton.render(gc);
        backButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        updateButtons(inputProvider);

        if (infiniteButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.INFINITE_MODE)
            );
        } else if (levelsButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.LEVEL_STATE)
            );
        } else if (backButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        } else if (inputProvider.isKeyPressed(KeyCode.ESCAPE)) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }
    }
}