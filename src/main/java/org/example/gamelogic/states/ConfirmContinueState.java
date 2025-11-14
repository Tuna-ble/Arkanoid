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

public final class ConfirmContinueState implements GameState {

    private final AbstractButton continueButton;
    private final AbstractButton resetButton;
    private final int levelId;
    private final Font titleFont = new Font("Arial", 48);
    private final double centerX;

    public ConfirmContinueState(int levelId) {
        this.levelId = levelId;
        this.centerX = GameConstants.SCREEN_WIDTH / 2.0;

        double buttonWidth = GameConstants.UI_BUTTON_WIDTH + 50;
        double buttonX = this.centerX - buttonWidth / 2;

        AssetManager am = AssetManager.getInstance();
        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");
        this.continueButton = new Button(
                buttonX,
                GameConstants.SCREEN_HEIGHT / 2.0 - GameConstants.UI_BUTTON_HEIGHT - 10,
                buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT,
                normalImage,
                hoveredImage,
                "Continue"
        );

        this.resetButton = new Button(
                buttonX,
                GameConstants.SCREEN_HEIGHT / 2.0 + 10,
                buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT,
                normalImage,
                hoveredImage,
                "Reset Level"
        );
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.color(0, 0, 0, 0.7)); // Màu đen mờ 70%
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc, "Load Progress?", this.centerX, 200, titleFont,
                Color.WHITE, Color.BLACK, 2.0, null
        );

        continueButton.render(gc);
        resetButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        continueButton.handleInput(inputProvider);
        resetButton.handleInput(inputProvider);

        if (continueButton.isClicked()) {
            SaveGameRepository repo = new SaveGameRepository();
            SavedGameState savedData = repo.loadGame(levelId);

            if (savedData != null) {
                repo.deleteSave(levelId);
                GameManager gm = GameManager.getInstance();
                PlayingState playingState = new PlayingState(gm, levelId);
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