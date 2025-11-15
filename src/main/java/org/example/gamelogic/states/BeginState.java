package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.DropShadow;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.text.TextRenderer;

public final class BeginState implements GameState {
    private Image backgroundImage;
    private Image normalImage;
    private Image hoveredImage;
    private AbstractButton signInButton;
    private AbstractButton registerButton;
    private double elapsedTime = 0;

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private final double baseY = GameConstants.SCREEN_HEIGHT / 2.0 - 40;

    public BeginState() {
        AssetManager am = AssetManager.getInstance();
        this.backgroundImage = am.getImage("mainMenu");
        this.normalImage = am.getImage("button");
        this.hoveredImage = am.getImage("hoveredButton");

        double buttonGap = 70;

        signInButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                baseY + (buttonGap * 0),
                normalImage,
                hoveredImage,
                "Sign In"
        );

        registerButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                baseY + (buttonGap * 1),
                normalImage,
                hoveredImage,
                "Register"
        );
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
        if (backgroundImage != null) {
            gc.drawImage(
                    backgroundImage,
                    0,
                    0,
                    GameConstants.SCREEN_WIDTH,
                    GameConstants.SCREEN_HEIGHT
            );
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        }

        // Title
        gc.setTextAlign(TextAlignment.CENTER);
        LinearGradient titleFill = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#66ccff")),
                new Stop(1, Color.web("#228BE6"))
        );
        DropShadow titleShadow = new DropShadow(12, Color.color(0, 0, 0, 0.6));
        Font titleFont = Font.font("Verdana", FontWeight.BOLD, 52);

        TextRenderer.drawOutlinedText(
                gc,
                "ACCOUNT",
                GameConstants.SCREEN_WIDTH / 2.0,
                200,
                titleFont,
                titleFill,
                Color.color(0, 0, 0, 0.8),
                2.5,
                titleShadow
        );

        // Buttons
        if (signInButton != null) {
            signInButton.render(gc);
        }
        if (registerButton != null) {
            registerButton.render(gc);
        }

        // Hint text (nhấp nháy)
        if ((int) (elapsedTime * 2) % 2 == 0) {
            gc.setFont(new Font("Arial", 16));
            gc.setFill(Color.WHITE);
            gc.fillText(
                    "Press ESC to Exit",
                    GameConstants.SCREEN_WIDTH / 2.0,
                    GameConstants.SCREEN_HEIGHT - 40
            );
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) {
            return;
        }

        updateButtons(inputProvider);

        if (signInButton != null && signInButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.SIGN_IN)
            );
        } else if (registerButton != null && registerButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.REGISTER)
            );
        } else if (inputProvider.isKeyPressed(KeyCode.ESCAPE)) {
            System.exit(0);
        }
    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (signInButton != null) {
            signInButton.handleInput(inputProvider);
        }
        if (registerButton != null) {
            registerButton.handleInput(inputProvider);
        }
    }
}
