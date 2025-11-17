package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.data.SaveGameRepository;
import org.example.data.SavedGameState;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.PopupTransitionStrategy;

/**
 * Quản lý trạng thái "Xác nhận Tiếp tục" (Confirm Continue).
 * <p>
 * Lớp này hiển thị một cửa sổ (Window) khi người chơi
 * chọn một level (trong LevelState) mà có file save tồn tại.
 * Nó hỏi người chơi muốn "Continue" (tải save)
 * hay "Reset Level" (xóa save và chơi mới).
 */
public final class ConfirmContinueState implements GameState {
    private final Window window;
    private final AbstractButton continueButton;
    private final AbstractButton resetButton;
    private final int levelId;
    private final Font titleFont;
    private final double centerX;

    /**
     * Khởi tạo trạng thái Xác nhận Tiếp tục.
     * <p>
     * <b>Định nghĩa:</b> Lưu {@code levelId} (level có file save).
     * Khởi tạo {@link Window} với hiệu ứng transition.
     * Tải tài nguyên (font, ảnh nút) và
     * tạo hai nút "Continue" và "Reset Level".
     * <p>
     * <b>Expected:</b> Cửa sổ xác nhận được tạo,
     * chứa các nút, và sẵn sàng cho việc update/render.
     *
     * @param levelId Level (int) có file save cần xác nhận.
     */
    public ConfirmContinueState(int levelId) {
        ITransitionStrategy transition = new PopupTransitionStrategy();
        this.window = new Window(null, 500, 400, transition);

        this.levelId = levelId;
        this.centerX = GameConstants.SCREEN_WIDTH / 2.0;

        double buttonWidth = GameConstants.UI_BUTTON_WIDTH + 50;
        double buttonX = this.centerX - buttonWidth / 2;
        double buttonY = window.getY() + 150;

        AssetManager am = AssetManager.getInstance();
        titleFont = am.getFont("Anxel", 45);

        final Image normalImage = am.getImage("selectButton");
        final Image hoveredImage = am.getImage("selectButtonHovered");

        this.continueButton = new Button(
                buttonX,
                buttonY,
                buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT,
                normalImage,
                hoveredImage,
                "Continue"
        );

        this.resetButton = new Button(
                buttonX,
                buttonY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_PADDING,
                buttonWidth,
                GameConstants.UI_BUTTON_HEIGHT,
                normalImage,
                hoveredImage,
                "Reset Level"
        );

        window.addButton(continueButton);
        window.addButton(resetButton);
    }

    /**
     * Cập nhật trạng thái Xác nhận Tiếp tục.
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
     * Vẽ (render) trạng thái Xác nhận Tiếp tục.
     * <p>
     * <b>Định nghĩa:</b> Xóa màn hình và gọi {@code window.render()}.
     * Nếu transition hoàn tất, vẽ văn bản tiêu đề ("Load Progress?").
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
                    gc, "Load Progress?", this.centerX, 250, titleFont,
                    Color.WHITE, Color.BLACK, 2.0, null
            );
        }
    }

    /**
     * Xử lý input (click chuột) của người dùng.
     * <p>
     * <b>Định nghĩa:</b> Ủy quyền (delegate) xử lý input cho {@link Window}
     * (để cập nhật trạng thái nút).
     * Kiểm tra click cho nút "Continue" và "Reset Level".
     * <p>
     * <b>Expected:</b>
     * <ul>
     * <li>Click "Continue": Tải {@code SavedGameState},
     * xóa file save, tạo {@link PlayingState} mới,
     * gọi {@code playingState.loadGame(savedData)},
     * và chuyển sang PlayingState.</li>
     * <li>Click "Reset Level": Xóa file save và
     * gọi {@link #startNewGame()}.</li>
     * </ul>
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        window.handleInput(inputProvider);

        if (continueButton.isClicked()) {
            SaveGameRepository repo = new SaveGameRepository();
            SavedGameState savedData = repo.loadGame(levelId);

            if (savedData != null) {
                repo.deleteSave(levelId);
                GameManager gm = GameManager.getInstance();
                PlayingState playingState = new PlayingState(gm, GameModeEnum.LEVEL, levelId, true);
                playingState.loadGame(savedData);

                gm.getStateManager().setState(playingState);

            } else {
                startNewGame();
            }
        }

        if (resetButton.isClicked()) {
            SaveGameRepository repo = new SaveGameRepository();

            repo.deleteSave(levelId);

            startNewGame();
        }
    }

    /**
     * (Helper) Bắt đầu một game mới cho level đã chọn.
     * <p>
     * <b>Định nghĩa:</b> Phát sự kiện {@link ChangeStateEvent}
     * để chuyển sang trạng thái {@code PLAYING} với {@code levelId} hiện tại.
     * <p>
     * <b>Expected:</b> Trạng thái game chuyển sang {@code PLAYING}.
     */
    private void startNewGame() {

        EventManager.getInstance().publish(
                new ChangeStateEvent(GameStateEnum.PLAYING, levelId)
        );
    }
}