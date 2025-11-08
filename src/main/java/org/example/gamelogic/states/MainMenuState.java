package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class MainMenuState implements GameState {
    private Image mainMenuImage;
    private Image gameTitle;
    private Button startButton;
    private Button rankingButton;
    private Button settingsButton;
    private Button newGameButton;
    private double elapsedTime = 0;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private final double baseY = GameConstants.SCREEN_HEIGHT / 2.0 + 75;

    public MainMenuState() {
        try {
            mainMenuImage = AssetManager.getInstance().getImage("main_menu");
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh MainMenu.gif từ resources!");
            e.printStackTrace();
        }
        try {
            gameTitle = AssetManager.getInstance().getImage("title");
        } catch (Exception e) {
            System.err.println("Không thể tải ảnh title.png từ resources!");
            e.printStackTrace();
        }
        double buttonGap = 70;
        startButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + (buttonGap * 0), "Start");
        rankingButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + (buttonGap * 1), "Ranking");
        settingsButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + (buttonGap * 2), "Settings");
        newGameButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + (buttonGap * 3), "Reset Game");
    }

    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.setTextAlign(TextAlignment.CENTER);
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        // Background
        if (mainMenuImage != null) {
            gc.drawImage(mainMenuImage, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        }

        // Title
        gc.drawImage(gameTitle, (GameConstants.SCREEN_WIDTH-800)/2, 0, 800, 450);

        // Buttons
        if (startButton != null) startButton.render(gc);
        if (rankingButton != null) rankingButton.render(gc);
        if (settingsButton != null) settingsButton.render(gc);
        if (newGameButton != null) newGameButton.render(gc);

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
                    new ChangeStateEvent(GameStateEnum.GAME_MODE)
            );
        } else if (rankingButton != null && rankingButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.RANKING_STATE)
            );
        } else if (settingsButton != null && settingsButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.SETTINGS)
            );
        } else if (newGameButton != null && newGameButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.CONFIRM_RESET)
            );
        } else if (inputProvider.isKeyPressed(KeyCode.ESCAPE)) {
            System.exit(0);
        }
    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (startButton != null) startButton.update(inputProvider);
        if (rankingButton != null) rankingButton.update(inputProvider);
        if (settingsButton != null) settingsButton.update(inputProvider);
        if (newGameButton != null) newGameButton.update(inputProvider);
    }

}


