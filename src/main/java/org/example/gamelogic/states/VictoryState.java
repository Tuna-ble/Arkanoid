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
import org.example.gamelogic.graphics.TextRenderer;

public final class VictoryState implements GameState {

    private final int livesLeft;
    private final int levelCompleted;
    private final int starsAwarded;

    private final AbstractButton quitButton;
    private final AbstractButton menuButton;
    private final AbstractButton nextButton;

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;


    private final Font titleFont = new Font("Arial", 70);
    private final Font starFont = new Font("Arial", 60);
    private final DropShadow titleShadow = new DropShadow(14, Color.color(0, 0, 0, 0.7));
    private final LinearGradient titleFill = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#88ff88")),
            new Stop(1, Color.web("#44cc44"))
    );

    private Image victory;

    public VictoryState(int livesLeft, int levelCompleted) {
        this.livesLeft = livesLeft;
        this.levelCompleted = levelCompleted;

        if (livesLeft >= 3) {
            this.starsAwarded = 3;
        } else if (livesLeft == 2) {
            this.starsAwarded = 2;
        } else {
            this.starsAwarded = 1;
        }

        if (this.starsAwarded > 0) {
            ProgressManager.saveProgress(this.levelCompleted, this.starsAwarded);
        }

        double buttonWidth = 180;
        double buttonSpacing = (GameConstants.SCREEN_WIDTH - (buttonWidth * 3)) / 4;
        double buttonY = GameConstants.SCREEN_HEIGHT - GameConstants.UI_BUTTON_HEIGHT - 80;
        double quitX = buttonSpacing;
        double menuX = quitX + buttonWidth + buttonSpacing;
        double nextX = menuX + buttonWidth + buttonSpacing;

        AssetManager am = AssetManager.getInstance();
        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");
        this.quitButton = new Button(quitX, buttonY, buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT, normalImage, hoveredImage, "Quit");
        this.menuButton = new Button(menuX, buttonY, buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT, normalImage, hoveredImage, "Menu");
        this.nextButton = new Button(nextX, buttonY, buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT, normalImage, hoveredImage, "Next Level");

        victory = new Image("/images/victory.gif");
    }

    @Override
    public void update(double deltaTime) {

    }

    private void updateButtons(I_InputProvider inputProvider) {
        quitButton.handleInput(inputProvider);
        menuButton.handleInput(inputProvider);
        nextButton.handleInput(inputProvider);
    }

    @Override
    public void render(GraphicsContext gc) {
        double scale = GameConstants.SCREEN_HEIGHT / victory.getHeight();
        double scaledWidth = victory.getWidth() * scale;

        double cropStartX = scaledWidth - GameConstants.SCREEN_WIDTH;

        gc.drawImage(
                victory,
                cropStartX / scale, 0, // sx, sy
                GameConstants.SCREEN_WIDTH / scale, victory.getHeight(), // sw, sh (crop gốc)
                0, 0, // dx, dy
                GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT // dw, dh (vẽ full screen)
        );

        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc,
                "VICTORY",
                centerX,
                150,
                titleFont,
                titleFill,
                Color.color(0,0,0,0.9),
                3.0,
                titleShadow
        );

        String starText = "";
        for(int i = 0; i < 3; i++) {
            if (i < starsAwarded) {
                starText += "★ ";
            } else {
                starText += "☆ ";
            }
        }

        gc.setFill(Color.YELLOW);
        gc.setFont(starFont);
        gc.fillText(starText, centerX, 280);


        quitButton.render(gc);
        menuButton.render(gc);
        nextButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        updateButtons(inputProvider);

        if (quitButton.isClicked()) {
            System.exit(0);
        }

        if (menuButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }

        if (nextButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PLAYING, levelCompleted + 1)
            );
        }
    }
}