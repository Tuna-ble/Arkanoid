package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ProgressManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;
import java.util.Map;

public final class LevelState implements GameState {

    private static final int NUM_LEVELS = 5;
    private final Image level;
    private double elapsedTime = 0;

    private final Button[] levelButtons;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private final int maxLevelUnlocked;
    private final Map<Integer, Integer> starsMap;

    private final Button backButton;

    public LevelState() {
        this.level = new Image("/GameIcon/level.gif");
        this.levelButtons = new Button[NUM_LEVELS];

        this.maxLevelUnlocked = ProgressManager.getMaxLevelUnlocked();
        this.starsMap = ProgressManager.loadStars();

        double baseY = 180;

        for (int i = 0; i < NUM_LEVELS; i++) {
            double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
            double btnY = baseY + i * (GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING);

            int currentLevel = i + 1;
            String btnText;

            if (currentLevel > maxLevelUnlocked) {
                btnText = "Level " + currentLevel + " (Locked)";
            } else {
                int stars = starsMap.getOrDefault(currentLevel, 0);
                btnText = "Level " + currentLevel + "  " + getStarString(stars);
            }

            levelButtons[i] = new Button(btnX, btnY, btnText);
        }

        double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double btnY = GameConstants.SCREEN_HEIGHT - GameConstants.UI_BUTTON_HEIGHT - 40;
        this.backButton = new Button(btnX, btnY, "Back");
    }

    private String getStarString(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i < stars) {
                sb.append("★");
            } else {
                sb.append("☆");
            }
        }
        return sb.toString();
    }

    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    private void updateButtons(I_InputProvider inputProvider) {
        for (Button btn : levelButtons) {
            if (btn != null) {
                btn.update(inputProvider);
            }
        }
        backButton.update(inputProvider);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(level, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        LinearGradient titleFill = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#8888ff")),
                new Stop(1, Color.web("#4444ff"))
        );
        DropShadow titleShadow = new DropShadow(14, Color.color(0, 0, 0, 0.7));
        Font titleFont = new Font("Arial", 70);
        TextRenderer.drawOutlinedText(
                gc, "CHOOSE LEVEL", centerX, 110, titleFont,
                titleFill, Color.color(0,0,0,0.9), 3.0, titleShadow
        );

        for (int i = 0; i < levelButtons.length; i++) {
            Button btn = levelButtons[i];
            if (btn == null) continue;

            int currentLevel = i + 1;
            if (currentLevel > maxLevelUnlocked) {
                gc.save();
                gc.setGlobalAlpha(0.5);
                btn.render(gc);
                gc.restore();
            } else {
                btn.render(gc);
            }
        }
        backButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        updateButtons(inputProvider);

        if (backButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.GAME_MODE)
            );
        }

        for (int i = 0; i < levelButtons.length; i++) {
            Button btn = levelButtons[i];
            if (btn == null || !btn.isClicked()) {
                continue;
            }

            int selectedLevel = i + 1;

            if (selectedLevel > maxLevelUnlocked) {
                continue;
            }
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PLAYING, selectedLevel)
            );
            break;
        }
    }
}