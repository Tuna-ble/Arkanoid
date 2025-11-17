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
import org.example.gamelogic.core.ScoreManager;
import org.example.gamelogic.core.SoundManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.TextRenderer;

/**
 * Quản lý trạng thái "Chiến thắng" (Victory) khi người chơi hoàn thành một màn.
 * <p>
 * Lớp này chịu trách nhiệm tính toán sao, hiển thị điểm,
 * render hoạt ảnh và xử lý input cho các nút (Menu, Restart, Next).
 */
public final class VictoryState implements GameState {
    private final int livesLeft;
    private final int levelCompleted;
    private final int starsAwarded;

    private final AbstractButton quitButton;
    private final AbstractButton menuButton;
    private final AbstractButton nextButton;
    private final AbstractButton restartButton;

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private final Font titleFont;
    private final Font starFont;
    private final Font scoreFont;
    private final DropShadow titleShadow = new DropShadow(14, Color.color(0, 0, 0, 0.7));
    private final LinearGradient titleFill = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#88ff88")),
            new Stop(1, Color.web("#44cc44"))
    );

    private Image victory;

    private double elapsedTime = 0.0;
    private final double STAR_ANIM_START_TIME = 0.5;
    private final double TIME_PER_STAR = 0.5;
    private final double BUTTON_FADE_IN_TIME = 1.0;

    private boolean star1SoundPlayed = false;
    private boolean star2SoundPlayed = false;
    private boolean star3SoundPlayed = false;

    /**
     * Khởi tạo trạng thái Victory.
     * <p>
     * <b>Định nghĩa:</b> Tính toán số sao đạt được dựa trên số mạng còn lại.
     * Lưu tiến trình (progress) vào {@link ProgressManager}.
     * Khởi tạo các nút bấm và tải tài nguyên (font, ảnh).
     * <p>
     * <b>Expected:</b> Trạng thái sẵn sàng để update và render,
     * tiến trình của người chơi đã được lưu.
     *
     * @param livesLeft      Số mạng còn lại của người chơi.
     * @param levelCompleted Cấp độ (level) vừa hoàn thành.
     */
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
        double buttonY = GameConstants.SCREEN_HEIGHT - GameConstants.UI_BUTTON_HEIGHT * 2 - 80;
        double menuX = buttonSpacing;
        double restartX = menuX + buttonWidth + buttonSpacing;
        double quitX = restartX;
        double nextX = restartX + buttonWidth + buttonSpacing;

        AssetManager am = AssetManager.getInstance();
        titleFont = am.getFont("Anxel", 70);
        starFont = am.getFont("Anxel", 60);
        scoreFont = am.getFont("Anxel", 40);

        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");
        this.quitButton = new Button(quitX, buttonY + GameConstants.UI_BUTTON_HEIGHT +
                GameConstants.UI_BUTTON_PADDING, buttonWidth, GameConstants.UI_BUTTON_HEIGHT,
                normalImage, hoveredImage, "Quit");
        this.restartButton = new Button(restartX, buttonY, buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT, normalImage, hoveredImage, "Restart");
        this.menuButton = new Button(menuX, buttonY, buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT, normalImage, hoveredImage, "Menu");
        this.nextButton = new Button(nextX, buttonY, buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT, normalImage, hoveredImage, "Next Level");

        victory = AssetManager.getInstance().getImage("victory");
    }

    /**
     * Cập nhật trạng thái Victory (chủ yếu là thời gian cho hoạt ảnh).
     * <p>
     * <b>Định nghĩa:</b> Tăng {@code elapsedTime} dựa trên {@code deltaTime}.
     * <p>
     * <b>Expected:</b> {@code elapsedTime} được cập nhật,
     * dùng cho render hoạt ảnh sao và nút bấm.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    /**
     * (Helper) Cập nhật trạng thái (hover, click) cho tất cả các nút bấm.
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code handleInput} trên từng đối tượng nút.
     * <p>
     * <b>Expected:</b> Trạng thái của các nút (quit, menu, next, restart)
     * được cập nhật theo input của người dùng.
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    private void updateButtons(I_InputProvider inputProvider) {
        quitButton.handleInput(inputProvider);
        menuButton.handleInput(inputProvider);
        nextButton.handleInput(inputProvider);
        restartButton.handleInput(inputProvider);
    }

    /**
     * Vẽ (render) trạng thái Victory lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ nền, tiêu đề "VICTORY", điểm số cuối cùng.
     * Render hoạt ảnh 3 ngôi sao dựa trên {@code elapsedTime}.
     * Render hoạt ảnh mờ dần (fade-in) cho các nút bấm.
     * <p>
     * <b>Expected:</b> Toàn bộ giao diện "Chiến thắng" được hiển thị
     * với các hiệu ứng hoạt ảnh.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        double scale = GameConstants.SCREEN_HEIGHT / victory.getHeight();
        double scaledWidth = victory.getWidth() * scale;

        double cropStartX = scaledWidth - GameConstants.SCREEN_WIDTH;

        gc.drawImage(
                victory,
                cropStartX / scale, 0,
                GameConstants.SCREEN_WIDTH / scale, victory.getHeight(),
                0, 0,
                GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT
        );

        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc,
                "VICTORY",
                centerX,
                230,
                titleFont,
                titleFill,
                Color.color(0, 0, 0, 0.9),
                3.0,
                titleShadow
        );

        int finalScore = ScoreManager.getInstance().getScore();
        TextRenderer.drawOutlinedText(
                gc,
                "Final Score: " + finalScore,
                centerX,
                370,
                scoreFont,
                Color.web("#ffffcc"),
                Color.color(0, 0, 0, 0.85),
                2.0,
                new DropShadow(8, Color.color(0, 0, 0, 0.6))
        );

        double starAnimProgress = elapsedTime - STAR_ANIM_START_TIME;

        double star1Time = TIME_PER_STAR;
        double star2Time = TIME_PER_STAR * 2;
        double star3Time = TIME_PER_STAR * 3;

        String star1 = "☆ ";
        String star2 = "☆ ";
        String star3 = "☆";

        if (starAnimProgress > star1Time && starsAwarded >= 1) {
            star1 = "★ ";
        }
        if (starAnimProgress > star2Time && starsAwarded >= 2) {
            star2 = "★ ";
        }
        if (starAnimProgress > star3Time && starsAwarded >= 3) {
            star3 = "★";
        }

        String starText = star1 + star2 + star3;

        gc.setFill(Color.YELLOW);
        gc.setFont(starFont);
        gc.fillText(starText, centerX, 310);

        double buttonAnimProgress = elapsedTime - (star3Time + 0.5);
        if (buttonAnimProgress > 0) {
            double buttonAlpha = Math.min(1.0, buttonAnimProgress / BUTTON_FADE_IN_TIME);

            gc.save();
            try {
                gc.setGlobalAlpha(buttonAlpha);

                quitButton.render(gc);
                menuButton.render(gc);
                restartButton.render(gc);
                if (levelCompleted < 5) {
                    nextButton.render(gc);
                }
            } finally {
                gc.restore();
            }
        }
    }

    /**
     * Xử lý input (click chuột) của người dùng.
     * <p>
     * <b>Định nghĩa:</b> Chặn input nếu hoạt ảnh sao chưa hoàn thành.
     * Gọi {@code updateButtons} và kiểm tra {@code isClicked()}
     * cho từng nút.
     * <p>
     * <b>Expected:</b> Phát sự kiện {@link ChangeStateEvent}
     * (để chuyển sang Menu, Restart, Next) hoặc thoát game (Quit)
     * khi nút tương ứng được click.
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        double starAnimEndTime = STAR_ANIM_START_TIME + (TIME_PER_STAR * 3);
        if (elapsedTime < starAnimEndTime) {
            return;
        }

        updateButtons(inputProvider);

        if (quitButton.isClicked()) {
            System.exit(0);
        }

        if (restartButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PLAYING, this.levelCompleted)
            );
        }

        if (menuButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }

        if (nextButton.isClicked() && levelCompleted < 5) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PLAYING, levelCompleted + 1)
            );
        }
    }
}