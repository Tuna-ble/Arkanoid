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
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ProgressManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.text.TextRenderer;
import org.example.data.SaveGameRepository;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.HologramTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ScrollDownTransitionStrategy;

import java.util.Map;

public final class LevelState implements GameState {
    private static final int NUM_LEVELS = 5;
    private final Image level;
    private final Image normalImage;
    private final Image hoveredImage;
    private double elapsedTime = 0;

    private final AbstractButton[] levelButtons;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private final int maxLevelUnlocked;
    private final Map<Integer, Integer> starsMap;

    private final AbstractButton backButton;

    private Window window;

    public LevelState() {
        ITransitionStrategy transition = new ScrollDownTransitionStrategy();
        this.window = new Window(null, 500, 450, transition,
                "LEVELS", null);

        AssetManager am = AssetManager.getInstance();
        this.level = new Image("/images/level.gif");
        this.normalImage = am.getImage("button");
        this.hoveredImage = am.getImage("hoveredButton");
        this.levelButtons = new AbstractButton[NUM_LEVELS];

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

            levelButtons[i] = new Button(btnX, btnY, normalImage, hoveredImage, btnText);
            levelButtons[i].setTransition(new WipeElementTransitionStrategy(0.5));

            this.window.addButton(levelButtons[i]);
        }

        double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double btnY = GameConstants.SCREEN_HEIGHT - GameConstants.UI_BUTTON_HEIGHT - 40;
        this.backButton = new Button(btnX, btnY, normalImage, hoveredImage, "Back");
        this.backButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.window.addButton(backButton);
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
        window.update(deltaTime);
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
            AbstractButton btn = levelButtons[i];
            if (btn == null) continue;

            int currentLevel = i + 1;
            if (currentLevel > maxLevelUnlocked) {
                btn.setDisabled(true);
            } else {
                btn.setDisabled(false);
            }
        }
        window.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        window.handleInput(inputProvider);

        if (backButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.GAME_MODE)
            );
        }

        for (int i = 0; i < levelButtons.length; i++) {
            AbstractButton btn = levelButtons[i];
            if (btn == null || !btn.isClicked()) {
                continue;
            }

            int selectedLevel = i + 1;

            if (selectedLevel > maxLevelUnlocked) {
                continue;
            }

            System.out.println(GameManager.AccountId);

            SaveGameRepository repo = new SaveGameRepository(GameManager.AccountId);

            if (repo.hasSave(selectedLevel)) {
                System.out.println("ôiii");
                GameManager gm = GameManager.getInstance();
                gm.getStateManager().setState(new ConfirmContinueState(selectedLevel));

            } else {
                System.out.println("vclon");
                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.PLAYING, selectedLevel)
                );
            }

            break;
        }
    }
}