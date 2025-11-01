package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.HighscoreManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

import java.util.List;

public final class RankingState implements GameState {

    private final List<Integer> highscores;
    private final Button backButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private final Font titleFont = new Font("Arial", 70);
    private final Font scoreFont = new Font("Arial", 40);
    private final DropShadow titleShadow = new DropShadow(14, Color.color(0, 0, 0, 0.7));
    private final LinearGradient titleFill = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#ffff88")),
            new Stop(1, Color.web("#ffcc44"))
    );

    public RankingState() {
        this.highscores = HighscoreManager.loadHighscores();

        double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double btnY = GameConstants.SCREEN_HEIGHT - GameConstants.UI_BUTTON_HEIGHT - 60;
        this.backButton = new Button(btnX, btnY, "Back to Menu");
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc,
                "RANKING",
                centerX,
                110,
                titleFont,
                titleFill,
                Color.color(0,0,0,0.9),
                3.0,
                titleShadow
        );

        double scoreY = 220;
        String[] rankLabels = {"1ST", "2ND", "3RD"};

        if (highscores.isEmpty()) {
            TextRenderer.drawOutlinedText(
                    gc,
                    "NO SCORES YET",
                    centerX,
                    scoreY,
                    scoreFont, Color.WHITE, Color.BLACK, 1.5, null
            );
        } else {
            for (int i = 0; i < highscores.size(); i++) {
                String text = rankLabels[i] + "   -   " + highscores.get(i);
                TextRenderer.drawOutlinedText(
                        gc,
                        text,
                        centerX,
                        scoreY + (i * 80),
                        scoreFont, Color.WHITE, Color.BLACK, 1.5, null
                );
            }
        }

        backButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        backButton.update(inputProvider);
        if (backButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }
    }
}