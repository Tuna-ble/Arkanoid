package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;

import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.HighscoreManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.TextRenderer;

import java.util.List;

public final class RankingState implements GameState {

    private final List<Integer> highscores;
    private final AbstractButton backButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private Image rankingIcon;

    private final Font titleFont = new Font("Arial", 70);
    private final Font scoreFont = new Font("Arial", 40);
    private final Font rankFont = new Font("Arial", 32);
    private double elapsed = 0.0;

    private final Color bgStart = Color.web("#FFF0F5");
    private final Color bgEnd = Color.web("#FFE4E1");
    private final Color scoreNormalColor = Color.web("#ffdd44");
    private final Color scoreHighlightColor = Color.web("#ffff44");
    private final LinearGradient titleGradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#ffff88")),
            new Stop(1, Color.web("#ffcc44"))
    );

    // Pre-create effects
    private final DropShadow normalScoreShadow = new DropShadow(8, Color.web("#ffdd44", 0.6));
    private final DropShadow titleBaseShadow = new DropShadow(20, Color.web("#ffdd44", 0.4));

    private void drawScoreEntry(GraphicsContext gc, double x, double y,
                                        double width, double height, 
                                    int rank, int score) {
        Color cardBg = (rank == 1) ? Color.web("#FFE4E1", 0.4) : Color.web("#FFF0F5", 0.2);
        Color stroke = (rank == 1) ? Color.web("#FFB6C1", 0.9) : Color.web("#FFB6C1", 0.4);
        
        // Draw card background
        gc.setFill(cardBg);
        gc.fillRoundRect(x, y, width, height, 15, 15);
        
        gc.setStroke(stroke);
        gc.setLineWidth(rank == 1 ? 2.0 : 1.0);
        gc.strokeRoundRect(x, y, width, height, 15, 15);

        // Draw rank number and score with pre-created fonts
        gc.setTextAlign(TextAlignment.CENTER);
        
        Color textColor = (rank == 1) ? scoreHighlightColor : scoreNormalColor;
        TextRenderer.drawOutlinedText(
                gc,
                String.valueOf(rank),
                x + 50,
                y + height/2 + 10,
                rankFont,
                textColor,
                Color.web("#4A0404", 0.8),
                1.8,
                rank == 1 ? normalScoreShadow : null
        );

        // Draw score with more emphasis
        gc.setTextAlign(TextAlignment.RIGHT);
        TextRenderer.drawOutlinedText(
                gc,
                String.format("%,d", score),
                x + width - 30,
                y + height/2 + 10,
                scoreFont,
                textColor,
                Color.web("#4A0404", 0.8),
                2.0,
                rank == 1 ? normalScoreShadow : null
        );
    }

    public RankingState() {
        this.highscores = HighscoreManager.loadHighscores();
        org.example.data.AssetManager am = org.example.data.AssetManager.getInstance();
        this.rankingIcon = am.getImage("ranking");

        double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double btnY = GameConstants.SCREEN_HEIGHT - GameConstants.UI_BUTTON_HEIGHT - 40;
        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");
        this.backButton = new Button(btnX, btnY, normalImage, hoveredImage, "Back to Menu");
    }

    @Override
    public void update(double deltaTime) {
        elapsed += deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        if (rankingIcon != null) {
            gc.drawImage(rankingIcon, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        }

        gc.setTextAlign(TextAlignment.CENTER);
        double pulse = 0.75 + 0.25 * Math.abs(Math.sin(elapsed * 1.8));

        // Score entries
        double scoreY = 220;
        double listWidth = Math.min(800, GameConstants.SCREEN_WIDTH - 200);

        if (highscores.isEmpty()) {
            TextRenderer.drawOutlinedText(
                    gc,
                    "NO SCORES YET",
                    centerX,
                    scoreY,
                    scoreFont, 
                    scoreNormalColor, 
                    Color.web("#4A0404", 0.8), 
                    2.0, 
                    null
            );
        } else {
            int maxToShow = Math.min(highscores.size(), 5);
            for (int i = 0; i < maxToShow; i++) {
                double x = centerX - listWidth / 2.0;
                double y = scoreY + (i * 90); 
                drawScoreEntry(gc, x, y, listWidth, 70, i + 1, highscores.get(i));
            }
        }

        backButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        backButton.handleInput(inputProvider);
        if (backButton.isClicked()) {
            rankingIcon = null;
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }
    }
}