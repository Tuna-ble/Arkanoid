package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.data.SaveGameRepository;
import org.example.data.SavedGameState;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.PopupTransitionStrategy;

public final class ConfirmContinueState implements GameState {
    private final Window window;
    private final AbstractButton continueButton;
    private final AbstractButton resetButton;
    private final int levelId;
    private final Font titleFont;
    private final double centerX;

    public ConfirmContinueState(int levelId) {
        ITransitionStrategy transition = new PopupTransitionStrategy();
        this.window = new Window(null, 500, 400, transition);

        this.levelId = levelId;
        this.centerX = GameConstants.SCREEN_WIDTH / 2.0;

        double buttonWidth = GameConstants.UI_BUTTON_WIDTH + 50;
        double buttonX = this.centerX - buttonWidth / 2;
        double buttonY = window.getY() + 150;

        AssetManager am = AssetManager.getInstance();
        titleFont = am.getFont("Anxel", 45);

        final Image normalImage = am.getImage("selectButton");
        final Image hoveredImage = am.getImage("selectButtonHovered");

        this.continueButton = new Button(
                buttonX,
                buttonY,
                buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT,
                normalImage,
                hoveredImage,
                "Continue"
        );

        this.resetButton = new Button(
                buttonX,
                buttonY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_PADDING,
                buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT,
                normalImage,
                hoveredImage,
                "Reset Level"
        );

        window.addButton(continueButton);
        window.addButton(resetButton);
    }

    @Override
    public void update(double deltaTime) {
        window.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        window.render(gc);

        if (window.transitionFinished()) {
            gc.setTextAlign(TextAlignment.CENTER);
            TextRenderer.drawOutlinedText(
                    gc, "Load Progress?", this.centerX, 250, titleFont,
                    Color.WHITE, Color.BLACK, 2.0, null
            );
        }
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        window.handleInput(inputProvider);

        if (continueButton.isClicked()) {
            SaveGameRepository repo = new SaveGameRepository();
            SavedGameState savedData = repo.loadGame(levelId);

            if (savedData != null) {
                repo.deleteSave(levelId);
                GameManager gm = GameManager.getInstance();
                PlayingState playingState = new PlayingState(gm, GameModeEnum.LEVEL, levelId, true);
                playingState.loadGame(savedData);

                gm.getStateManager().setState(playingState);

            } else {
                startNewGame();
            }
        }

        if (resetButton.isClicked()) {
            SaveGameRepository repo = new SaveGameRepository();

            repo.deleteSave(levelId);

            startNewGame();
        }
    }

    private void startNewGame() {

        EventManager.getInstance().publish(
                new ChangeStateEvent(GameStateEnum.PLAYING, levelId)
        );
    }
}