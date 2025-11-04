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
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class LevelState implements GameState {

    private static final int NUM_LEVELS = 5;
    private final Image level;
    private double elapsedTime = 0;

    private final Button[] levelButtons;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    public LevelState() {

        this.level = new Image("/GameIcon/level.gif");
        this.levelButtons = new Button[NUM_LEVELS];
        double baseY = 180;

        for (int i = 0; i < NUM_LEVELS; i++) {
            double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
            double btnY = baseY + i * (GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING);
            String btnText = "Level " + (i + 1);

            levelButtons[i] = new Button(btnX, btnY, btnText);
        }
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
                gc,
                "CHOOSE LEVEL",
                centerX,
                110,
                titleFont,
                titleFill,
                Color.color(0,0,0,0.9),
                3.0,
                titleShadow
        );

        for (Button btn : levelButtons) {
            if (btn != null) {
                btn.render(gc);
            }
        }

        /*
        if ((int)(elapsedTime * 2) % 2 == 0) {
            gc.setTextAlign(TextAlignment.CENTER);
            TextRenderer.drawOutlinedText(
                    gc,
                    "Click a level to start",
                    centerX,
                    GameConstants.SCREEN_HEIGHT - 40,
                    new Font("Arial", 14),
                    Color.WHITE,
                    Color.color(0,0,0,0.85),
                    1.0,
                    new DropShadow(5, Color.color(0,0,0,0.5))
            );
        }
         */
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        updateButtons(inputProvider);
        for (int i = 0; i < levelButtons.length; i++) {
            Button btn = levelButtons[i];

            if (btn != null && btn.isClicked()) {
                int selectedLevel = i + 1;

                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.PLAYING, selectedLevel)
                );
                break;
            }
        }
    }
}