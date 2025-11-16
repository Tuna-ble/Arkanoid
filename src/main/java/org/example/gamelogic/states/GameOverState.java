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
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
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

    private final double FADE_IN_DURATION = 1.5;

    public GameOverState(int levelToRestart) {
        this.levelToRestart = levelToRestart;

        AssetManager am = AssetManager.getInstance();
        gameOverGif = am.getImage("gameOver");
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

        double lineOffset = (elapsedTime * 100) % 4.0;
        gc.setGlobalAlpha(0.3);
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(1);
        for (double y = lineOffset; y < GameConstants.SCREEN_HEIGHT; y += 4.0) {
            gc.strokeLine(0, y, GameConstants.SCREEN_WIDTH, y);
        }

        gc.setGlobalAlpha(1.0);

        double alpha = Math.min(1.0, elapsedTime / FADE_IN_DURATION);
        gc.setGlobalAlpha(alpha);

        gc.setTextAlign(TextAlignment.CENTER);
        Color flickerColor = Color.web("#ff4444");
        if (Math.random() < 0.1) { // (10% cơ hội nháy sang màu trắng)
            flickerColor = Color.WHITE;
        }

        LinearGradient titleFill = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ff8888")),
                new Stop(1, flickerColor)
        );
        DropShadow titleShadow = new DropShadow(14, Color.color(0, 0, 0, 0.7));
        Font titleFont = AssetManager.getInstance().getFont("Anxel", 70);

        double glitchX = centerX + (Math.random() - 0.5) * 10;
        double glitchY = 230 + (Math.random() - 0.5) * 4;

        TextRenderer.drawOutlinedText(
                gc,
                "GAME OVER",
                glitchX,
                glitchY,
                titleFont,
                titleFill,
                Color.color(0,0,0,0.9),
                3.0,
                titleShadow
        );

        int finalScore = ScoreManager.getInstance().getScore();
        Font scoreFont = AssetManager.getInstance().getFont("Anxel", 40);
        TextRenderer.drawOutlinedText(
                gc,
                "Final Score: " + finalScore,
                centerX,
                280,
                scoreFont,
                Color.web("#ffffcc"),
                Color.color(0,0,0,0.85),
                2.0,
                new DropShadow(8, Color.color(0,0,0,0.6))
        );

        if (restartButton != null) restartButton.render(gc);
        if (menuButton != null) menuButton.render(gc);
        if (exitButton != null) exitButton.render(gc);
    }


    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        if (elapsedTime < FADE_IN_DURATION) {
            return;
        }

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
        if (restartButton != null) restartButton.handleInput(inputProvider);
        if (menuButton != null) menuButton.handleInput(inputProvider);
        if (exitButton != null) exitButton.handleInput(inputProvider);
    }
}
