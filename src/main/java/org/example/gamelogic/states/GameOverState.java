package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ScoreManager;
import org.example.gamelogic.I_InputProvider;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Buttons.AbstractButton;
import org.example.gamelogic.graphics.Buttons.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class GameOverState implements GameState {
    private Image gameOverGif;
    private final int levelToRestart;
    private double elapsedTime = 0;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private final double baseY = GameConstants.SCREEN_HEIGHT / 2.0 - 30;

    // Button instances
    private AbstractButton restartButton;
    private AbstractButton menuButton;
    private AbstractButton exitButton;

    public GameOverState(int levelToRestart) {
        this.levelToRestart = levelToRestart;
        gameOverGif = new Image("/GameIcon/gameOverBackground.gif");

        AssetManager am = AssetManager.getInstance();
        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");
        restartButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                baseY + 0,
                normalImage,
                hoveredImage,
                "Restart");
        menuButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                baseY + 80,
                normalImage,
                hoveredImage,
                "Menu");
        exitButton = new Button(centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                baseY + 160,
                normalImage,
                hoveredImage,
                "Exit");
    }

    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(gameOverGif, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        LinearGradient titleFill = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ff8888")),
                new Stop(1, Color.web("#ff4444"))
        );
        DropShadow titleShadow = new DropShadow(14, Color.color(0, 0, 0, 0.7));
        Font titleFont = new Font("Arial", 70);
        TextRenderer.drawOutlinedText(
                gc,
                "GAME OVER",
                centerX,
                150,
                titleFont,
                titleFill,
                Color.color(0,0,0,0.9),
                3.0,
                titleShadow
        );

        // Điểm
        int finalScore = ScoreManager.getInstance().getScore();
        Font scoreFont = new Font("Arial", 32);
        TextRenderer.drawOutlinedText(
                gc,
                "Final Score: " + finalScore,
                centerX,
                200,
                scoreFont,
                Color.web("#ffffcc"),
                Color.color(0,0,0,0.85),
                2.0,
                new DropShadow(8, Color.color(0,0,0,0.6))
        );

        // Button
        if (restartButton != null) restartButton.render(gc);
        if (menuButton != null) menuButton.render(gc);
        if (exitButton != null) exitButton.render(gc);

        // Nháy nhẹ phần gợi ý
        if ((int)(elapsedTime * 2) % 2 == 0) {
            gc.setTextAlign(TextAlignment.CENTER);
            TextRenderer.drawOutlinedText(
                    gc,
                    "Click a button to continue",
                    centerX,
                    GameConstants.SCREEN_HEIGHT - 40,
                    new Font("Arial", 14),
                    Color.WHITE,
                    Color.color(0,0,0,0.85),
                    1.0,
                    new DropShadow(5, Color.color(0,0,0,0.5))
            );
        }

    }


    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        
        // Update buttons to check hover and click states
        updateButtons(inputProvider);
        
        // Handle button clicks
        if (restartButton != null && restartButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PLAYING, this.levelToRestart)
            );
        } else if (menuButton != null && menuButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        } else if (exitButton != null && exitButton.isClicked()) {
            System.exit(0);
        }
    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (restartButton != null) restartButton.update(inputProvider);
        if (menuButton != null) menuButton.update(inputProvider);
        if (exitButton != null) exitButton.update(inputProvider);
    }
}
