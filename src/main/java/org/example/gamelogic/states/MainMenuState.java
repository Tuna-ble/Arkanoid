package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import javafx.scene.text.TextAlignment;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.DropShadow;
import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class MainMenuState implements GameState {
    private Image mainMenuImage;
    private Button startButton;
    private Button levelButton;
    private Button rankingButton;
    private Button settingsButton;
    private double elapsedTime = 0;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private final double baseY = GameConstants.SCREEN_HEIGHT / 2.0 - 30;

    public MainMenuState() {
        mainMenuImage = new Image(getClass().getResourceAsStream("/GameIcon/MainMenu.png"));
        startButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + 0, "Start");
        levelButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + 70, "Level");
        rankingButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + 140, "Ranking");
        settingsButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + 210, "Settings");
    }

    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.setTextAlign(TextAlignment.LEFT);
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        // Background
        gc.drawImage(mainMenuImage, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        // Title (outlined with subtle shadow and gradient)
        gc.setTextAlign(TextAlignment.CENTER);
        LinearGradient titleFill = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#66ccff")),
                new Stop(1, Color.web("#228BE6"))
        );
        DropShadow titleShadow = new DropShadow(12, Color.color(0, 0, 0, 0.6));
        Font titleFont = Font.font("Verdana", FontWeight.BOLD, 72);
        TextRenderer.drawOutlinedText(
                gc,
                "ARKANOID",
                GameConstants.SCREEN_WIDTH / 2.0,
                230,
                titleFont,
                titleFill,
                Color.color(0, 0, 0, 0.8),
                2.5,
                titleShadow
        );

        // Buttons
        if (startButton != null) startButton.render(gc);
        if (levelButton != null) levelButton.render(gc);
        if (rankingButton != null) rankingButton.render(gc);
        if (settingsButton != null) settingsButton.render(gc);

        // Nháy nhẹ phần gợi ý thoát
        if ((int) (elapsedTime * 2) % 2 == 0) {
            gc.setFont(new Font("Arial", 16));
            gc.setFill(Color.WHITE);
            gc.fillText("Press ESC to Exit", GameConstants.SCREEN_WIDTH / 2.0, GameConstants.SCREEN_HEIGHT - 40);
        }
        gc.setTextAlign(TextAlignment.LEFT);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        updateButtons(inputProvider);

        if ((startButton != null && startButton.isClicked())
                || inputProvider.isKeyPressed(KeyCode.SPACE)) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PLAYING, 1)
            );
        } else if (levelButton != null && levelButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.LEVEL_STATE)
            );
        } else if (rankingButton != null && rankingButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.RANKING_STATE)
            );
        } else if (settingsButton != null && settingsButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.SETTINGS)
            );
        } else if (inputProvider.isKeyPressed(KeyCode.ESCAPE)) {
            System.exit(0);
        }
    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (startButton != null) startButton.update(inputProvider);
        if (levelButton != null) levelButton.update(inputProvider);
        if (rankingButton != null) rankingButton.update(inputProvider);
        if (settingsButton != null) settingsButton.update(inputProvider);
    }

}


