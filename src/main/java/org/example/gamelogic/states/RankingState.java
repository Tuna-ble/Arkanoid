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

/**
 * Quản lý trạng thái "Bảng xếp hạng" (Ranking) của game.
 * <p>
 * Lớp này chịu trách nhiệm tải và hiển thị danh sách điểm cao (high scores)
 * và xử lý việc quay lại Main Menu.
 */
public final class RankingState implements GameState {

    private final List<Integer> highscores;
    private final AbstractButton backButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private Image rankingIcon;
    private Image scoreCardImage;

    private final Font titleFont;
    private final Font scoreFont;
    private final Font rankFont;
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

    /**
     * (Helper) Vẽ một mục (entry) điểm cao lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ ảnh nền (score card) và hiển thị
     * thứ hạng (rank) cùng điểm số (score) tại vị trí đã cho.
     * <p>
     * <b>Expected:</b> Một hàng điểm cao được vẽ lên `gc`,
     * nổi bật nếu là hạng 1.
     *
     * @param gc     Context (bút vẽ) của canvas.
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     * @param rank   Thứ hạng (1, 2, 3...).
     * @param score  Điểm số.
     */
    private void drawScoreEntry(GraphicsContext gc, double x, double y,
                                double width, double height,
                                int rank, int score) {
        if (scoreCardImage != null) {
            gc.drawImage(scoreCardImage, x, y, width, height);
        } else {
            gc.setFill(Color.web("#333", 0.5));
            gc.fillRect(x, y, width, height);
        }

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

    /**
     * Khởi tạo trạng thái Bảng xếp hạng.
     * <p>
     * <b>Định nghĩa:</b> Tải danh sách highscores từ {@link HighscoreManager}.
     * Tải tài nguyên (ảnh, font) và khởi tạo nút "Back to Menu".
     * <p>
     * <b>Expected:</b> Trạng thái sẵn sàng để update và render,
     * highscores đã được tải.
     */
    public RankingState() {
        this.highscores = HighscoreManager.loadHighscores();
        org.example.data.AssetManager am = org.example.data.AssetManager.getInstance();
        this.rankingIcon = am.getImage("ranking");

        this.titleFont = am.getFont("Anxel", 70);
        this.scoreFont = am.getFont("Anxel", 40);
        this.rankFont = am.getFont("Anxel", 32);

        double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double btnY = GameConstants.SCREEN_HEIGHT - GameConstants.UI_BUTTON_HEIGHT - 40;
        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");
        this.backButton = new Button(btnX, btnY, normalImage, hoveredImage, "Back to Menu");
        this.scoreCardImage = am.getImage("rankBanner");
    }

    /**
     * Cập nhật trạng thái Bảng xếp hạng.
     * <p>
     * <b>Định nghĩa:</b> Tăng {@code elapsed} (thời gian trôi qua)
     * dựa trên {@code deltaTime} để dùng cho hoạt ảnh.
     * <p>
     * <b>Expected:</b> {@code elapsed} được cập nhật.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        elapsed += deltaTime;
    }

    /**
     * Vẽ (render) trạng thái Bảng xếp hạng lên canvas.
     * <p>
     * <b>Định nghĩa:</b> Vẽ nền, tiêu đề "RANKING".
     * Lặp qua danh sách highscores (tối đa 5) và
     * gọi {@code drawScoreEntry} cho mỗi mục. Vẽ nút "Back".
     * <p>
     * <b>Expected:</b> Giao diện bảng xếp hạng được hiển thị đầy đủ.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
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
        double top1Height = 70;
        double top1Width = 650;

        double normalHeight = 70;
        double normalWidth = 600;

        TextRenderer.drawOutlinedText(
                gc,
                "RANKING",
                centerX,
                scoreY,
                titleFont,
                scoreHighlightColor,
                Color.web("#4A0404", 0.8),
                2.0,
                null
        );

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
                double width = normalWidth;
                double height = normalHeight;
                if(i == 0) {
                    width = top1Width;
                    height = top1Height;
                }
                double x = centerX - width / 2.0;
                double y = scoreY + 40 + (i * 90);
                drawScoreEntry(gc, x, y, width, height, i + 1, highscores.get(i));
            }
        }

        backButton.render(gc);
    }

    /**
     * Xử lý input (click chuột) của người dùng.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật trạng thái nút "Back"
     * ({@code backButton.handleInput}).
     * <p>
     * <b>Expected:</b> Nếu nút "Back" được click,
     * phát sự kiện {@link ChangeStateEvent}
     * để chuyển về {@code GameStateEnum.MAIN_MENU}.
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
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