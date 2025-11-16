package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.KeyCode;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.presentation.SpriteAnimation;

public final class GameModeState implements GameState {

    private boolean isHoveringLeft = false;
    private boolean isHoveringRight = false;

    private Image frameImage;
    private Image bgLeft;
    private Image bgRight;

    private Image infiniteStatic;
    private SpriteAnimation infiniteAnim;
    private Image casualStatic;
    private SpriteAnimation casualAnim;

    private final AbstractButton backButton;

    private final Font titleFont = new Font("Arial", 50);
    private final Font descFont = new Font("Arial", 18);

    private final double SCALE_NORMAL = 1.0;
    private final double SCALE_HOVER = 1.2;

    public GameModeState() {
        AssetManager am = AssetManager.getInstance();

        this.frameImage = am.getImage("modesFrame");
        this.bgLeft = am.getImage("victory");
        this.bgRight = am.getImage("victory");

        this.infiniteStatic = am.getImage("infiniteStatic");
        this.casualStatic = am.getImage("boss");

        Image infiniteSheet = am.getImage("infinite");
        if (infiniteSheet != null) {
            this.infiniteAnim = new SpriteAnimation(infiniteSheet, 32, 32, 1.0, true);
        }

        Image casualSheet = am.getImage("bossShoot");
        if (casualSheet != null) {
            this.casualAnim = new SpriteAnimation(casualSheet, 3, 3, 1.0, true);
        }

        Image btnImg = am.getImage("button");
        Image btnHover = am.getImage("hoveredButton");
        this.backButton = new Button(
                GameConstants.SCREEN_WIDTH / 2.0 - 100,
                GameConstants.SCREEN_HEIGHT - 100,
                200, 60,
                btnImg, btnHover, "Back to Menu"
        );
    }

    @Override
    public void update(double deltaTime) {
        if (isHoveringLeft && infiniteAnim != null) {
            infiniteAnim.update(deltaTime);
        }
        if (isHoveringRight && casualAnim != null) {
            casualAnim.update(deltaTime);
        }
    }

    private void renderHalf(GraphicsContext gc, boolean isHovered,
                            double x, double y, double w, double h,
                            Image background,
                            Image staticEntity,
                            SpriteAnimation animSpriteEntity,
                            double entityW, double entityH,
                            String title, String description) {

        gc.drawImage(background, x, y, w, h);

        double entityCenterX = x + w / 2.0;
        double entityCenterY = y + h / 2.0 + 50;

        double scale = isHovered ? SCALE_HOVER : SCALE_NORMAL;
        double scaledW = entityW * scale;
        double scaledH = entityH * scale;
        double scaledX = entityCenterX - scaledW / 2.0;
        double scaledY = entityCenterY - scaledH / 2.0;

        Image imageToDraw = null;
        SpriteAnimation animToDraw = null;

        if (isHovered) {
            if (animSpriteEntity != null) animToDraw = animSpriteEntity;
        } else {
            imageToDraw = staticEntity;
        }

        if (isHovered && animToDraw == null && imageToDraw == null) {
            imageToDraw = staticEntity;
        }

        if (animToDraw != null) {
            animToDraw.render(gc, scaledX, scaledY, scaledW, scaledH);
        } else {
            gc.drawImage(imageToDraw, scaledX, scaledY, scaledW, scaledH);
        }

        gc.drawImage(frameImage, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        Color titleColor = isHovered ? Color.CYAN : Color.WHITE;
        gc.setTextAlign(TextAlignment.CENTER);

        TextRenderer.drawOutlinedText(gc, title, x + w / 2, y + 150,
                titleFont, titleColor, Color.BLACK, 2.0, null);

        if(isHovered) {
            TextRenderer.drawOutlinedText(gc, description, x + w / 2, y + 200,
                    descFont, Color.WHITE, Color.BLACK, 1.0, null);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        double centerX = GameConstants.SCREEN_WIDTH / 2.0;
        double screenHeight = GameConstants.SCREEN_HEIGHT;

        renderHalf(gc, isHoveringLeft,
                0, 0, centerX, screenHeight,
                bgLeft,
                infiniteStatic,
                infiniteAnim,
                300, 300,
                "INFINITE", "Destroy bricks and defeat boss infinitely"); // Text

        renderHalf(gc, isHoveringRight,
                centerX, 0, centerX, screenHeight,
                bgRight,
                casualStatic,
                casualAnim,
                190, 350,
                "CASUAL", "Play through different levels");

        backButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        backButton.handleInput(inputProvider);

        int mouseX = inputProvider.getMouseX();
        boolean wasHoveringLeft = isHoveringLeft;
        boolean wasHoveringRight = isHoveringRight;

        if (backButton.isHovered()) {
            isHoveringLeft = false;
            isHoveringRight = false;
        } else if (mouseX < GameConstants.SCREEN_WIDTH / 2.0) {
            isHoveringLeft = true;
            isHoveringRight = false;
        } else {
            isHoveringLeft = false;
            isHoveringRight = true;
        }

        if (isHoveringLeft && !wasHoveringLeft && infiniteAnim != null) {
            infiniteAnim.reset();
        }
        if (isHoveringRight && !wasHoveringRight && casualAnim != null) {
            casualAnim.reset();
        }

        if (inputProvider.isMouseClicked()) {
            if (backButton.isClicked()) {
                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.MAIN_MENU)
                );
            } else if (isHoveringLeft) {
                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.INFINITE_MODE)
                );
            } else if (isHoveringRight) {
                EventManager.getInstance().publish(
                        new ChangeStateEvent(GameStateEnum.LEVEL_STATE)
                );
            }
        }

        if (inputProvider.isKeyPressed(KeyCode.ESCAPE)) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }
    }
}