package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.core.ScoreManager;
import org.example.presentation.InputHandler;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import org.example.presentation.TextRenderer;

public final class GameOverState implements GameState {
    private final GameManager gameManager;
    private double elapsedTime = 0;
    private Image gameOverGif; // Khai báo thêm ở class
    
    
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private final double baseY = GameConstants.SCREEN_HEIGHT / 2.0 - 30;

    public GameOverState(GameManager gameManager) {
        this.gameManager = gameManager;
        gameOverGif = new Image("/GameIcon/gameOverBackground.gif");
    }

    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(gameOverGif, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        // Tiêu đề (gradient, viền, shadow)
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

        // Các nút
        renderButton(gc,centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + 0, "Restart");
        renderButton(gc,centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + 80, "Menu");
        renderButton(gc,centerX - GameConstants.UI_BUTTON_WIDTH / 2, baseY + 160, "Exit");

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
        // Reset text alignment to avoid affecting subsequent states
        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
    }


    private void renderButton(GraphicsContext gc, double x, double y, String fallbackText) {
        gc.setFill(Color.color(0.13, 0.13, 0.13, 0.5)); // tương đương #222222 với độ mờ 50%
        gc.fillRoundRect(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT, 10, 10);
        gc.setStroke(Color.color(1, 1, 1, 0.8));
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT, 10, 10);
        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc,
                fallbackText,
                x + GameConstants.UI_BUTTON_WIDTH / 2,
                y + 38,
                new Font("Arial", 20),
                Color.WHITE,
                Color.color(0,0,0,0.85),
                1.5,
                new DropShadow(6, Color.color(0,0,0,0.6))
        );
    }

    @Override
    public void handleInput(InputHandler inputHandler) {
        if (inputHandler == null) return;

        if (inputHandler.isMouseClicked()) {
            int mouseX = inputHandler.getMouseX();
            int mouseY = inputHandler.getMouseY();
            if (isInButton(mouseX, mouseY, baseY)) {
                gameManager.setState(new PlayingState(gameManager, 1));
            } else if (isInButton(mouseX, mouseY, baseY + 80)) {
                gameManager.setState(new MainMenuState(gameManager));
            } else if (isInButton(mouseX, mouseY, baseY + 160)) {
                System.exit(0);
            }
        }
    }

    private boolean isInButton(int x, int y, double buttonY) {
        double buttonX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        return x >= buttonX && x <= buttonX + GameConstants.UI_BUTTON_WIDTH &&
                y >= buttonY && y <= buttonY + GameConstants.UI_BUTTON_HEIGHT;
    }
}
