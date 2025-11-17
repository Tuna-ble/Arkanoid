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
import org.example.gamelogic.core.HighscoreManager;
import org.example.gamelogic.core.ProgressManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.HologramTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.PopupTransitionStrategy;

/**
 * Quản lý trạng thái "Xác nhận Reset" (Confirm Reset).
 * <p>
 * Lớp này hiển thị một cửa sổ (Window) hỏi người chơi
 * có chắc chắn muốn xóa toàn bộ tiến trình (progress)
 * và điểm cao (highscores) hay không.
 */
public final class ConfirmResetState implements GameState {
    private final Window window;
    private final AbstractButton yesButton;
    private final AbstractButton noButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private final Font titleFont;
    private final Font textFont;
    private final DropShadow titleShadow = new DropShadow(10, Color.color(0, 0, 0, 0.7));
    private final LinearGradient titleFill = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#ff8888")),
            new Stop(1, Color.web("#ff4444"))
    );

    /**
     * Khởi tạo trạng thái Xác nhận Reset.
     * <p>
     * <b>Định nghĩa:</b> Khởi tạo {@link Window} với hiệu ứng transition.
     * Tải tài nguyên (font, ảnh nút) và tạo hai nút "YES" và "NO".
     * <p>
     * <b>Expected:</b> Cửa sổ xác nhận được tạo,
     * chứa các nút, và sẵn sàng cho việc update/render.
     */
    public ConfirmResetState() {
        double buttonY = 350;
        double buttonSpacing = 40;

        ITransitionStrategy transition = new HologramTransitionStrategy();
        this.window = new Window(null, 500, 500, transition);

        AssetManager am = AssetManager.getInstance();
        titleFont = am.getFont("Anxel", 45);
        textFont = am.getFont("Anxel", 30);
        final Image normalImage = am.getImage("selectButton");
        final Image hoveredImage = am.getImage("selectButtonHovered");
        this.yesButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                buttonY,
                normalImage,
                hoveredImage,
                "YES"
        );
        this.yesButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.noButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                buttonY + GameConstants.UI_BUTTON_HEIGHT + buttonSpacing,
                normalImage,
                hoveredImage,
                "NO"
        );
        this.noButton.setTransition(new WipeElementTransitionStrategy(0.5));

        window.addButton(yesButton);
        window.addButton(noButton);
    }

    /**
     * Cập nhật trạng thái Xác nhận Reset.
     * <p>
     * <b>Định nghĩa:</b> Ủy quyền (delegate) logic update
     * cho {@link Window} (để chạy transition).
     * <p>
     * <b>Expected:</b> Hiệu ứng transition của cửa sổ được cập nhật.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    @Override
    public void update(double deltaTime) {
        window.update(deltaTime);
    }

    /**
     * Vẽ (render) trạng thái Xác nhận Reset.
     * <p>
     * <b>Định nghĩa:</b> Xóa màn hình và gọi {@code window.render()}.
     * Nếu transition hoàn tất, vẽ văn bản cảnh báo ("ARE YOU SURE?").
     * Vẽ các nút "YES" và "NO".
     * <p>
     * <b>Expected:</b> Cửa sổ xác nhận
     * (bao gồm nền mờ, văn bản, và các nút) được vẽ lên canvas.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        window.render(gc);

        if (window.transitionFinished()) {
            gc.setTextAlign(TextAlignment.CENTER);
            TextRenderer.drawOutlinedText(
                    gc,
                    "ARE YOU SURE?",
                    centerX,
                    200,
                    titleFont,
                    titleFill,
                    Color.color(0, 0, 0, 0.9),
                    2.0,
                    titleShadow
            );
            TextRenderer.drawOutlinedText(
                    gc,
                    "This will delete ALL highscores\nand reset ALL level progress.",
                    centerX,
                    260,
                    textFont, Color.WHITE, Color.BLACK, 1.0, null
            );
        }

        yesButton.render(gc);
        noButton.render(gc);
    }

    /**
     * Xử lý input (click chuột) của người dùng.
     * <p>
     * <b>Định nghĩa:</b> Ủy quyền (delegate) xử lý input cho {@link Window}
     * (để cập nhật trạng thái nút). Kiểm tra click cho nút "YES" và "NO".
     * <p>
     * <b>Expected:</b> Nếu click "YES", gọi {@link HighscoreManager#resetHighscores()}
     * và {@link ProgressManager#resetProgress()}.
     * Cả hai nút "YES" và "NO" đều phát sự kiện
     * {@link ChangeStateEvent} (MAIN_MENU).
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        window.handleInput(inputProvider);

        if (yesButton.isClicked()) {
            HighscoreManager.resetHighscores();
            ProgressManager.resetProgress();
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        } else if (noButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        }
    }
}