package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.PopupTransitionStrategy;

/**
 * Quản lý trạng thái "Xác nhận Thoát về Menu" (Confirm Quit to Menu).
 * <p>
 * Lớp này hiển thị một cửa sổ (Window) cảnh báo người chơi
 * rằng tiến trình sẽ không được lưu và hỏi họ có muốn
 * tiếp tục thoát về Menu hay không ("Yes" / "No").
 */
public final class ConfirmQuitToMenuState implements GameState {
    private final GameState previousState;
    private final Window window;
    private final Font warningFont;
    private final Font messageFont;

    // Button layout uses GameConstants
    private Image buttonImage;
    private Image hoveredImage;

    // Center screen position
    private double centerX = GameConstants.SCREEN_WIDTH / 2;
    private double centerY = GameConstants.SCREEN_HEIGHT / 2;

    // Button instances
    private AbstractButton yesButton;
    private AbstractButton noButton;

    /**
     * Khởi tạo trạng thái Xác nhận Thoát về Menu.
     * <p>
     * <b>Định nghĩa:</b> Lưu trạng thái game trước đó
     * (thường là {@link PauseState}).
     * Khởi tạo {@link Window} với hiệu ứng transition.
     * Tải tài nguyên (font, ảnh nút) và tạo hai nút "Yes" và "No".
     * <p>
     * <b>Expected:</b> Cửa sổ xác nhận được tạo,
     * chứa các nút, và sẵn sàng cho việc update/render.
     *
     * @param previousState Trạng thái game ngay trước khi mở xác nhận.
     */
    public ConfirmQuitToMenuState(GameState previousState) {
        this.previousState = previousState;

        AssetManager am = AssetManager.getInstance();
        warningFont = am.getFont("Anxel", 45);
        messageFont = am.getFont("Anxel", 30);

        ITransitionStrategy transition = new PopupTransitionStrategy();
        this.window = new Window(previousState, 500, 400, transition);

        buttonImage = am.getImage("selectButton");
        hoveredImage = am.getImage("selectButtonHovered");

        double buttonX = window.getX() + window.getWidth() / 2 - GameConstants.UI_BUTTON_WIDTH / 2;
        double buttonY = window.getY() + 210;

        yesButton = new Button(buttonX, buttonY, buttonImage, hoveredImage, "Yes");
        yesButton.setTransition(new WipeElementTransitionStrategy(0.5));

        noButton = new Button(buttonX, buttonY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_PADDING,
                buttonImage, hoveredImage, "No");
        noButton.setTransition(new WipeElementTransitionStrategy(0.5));

        window.addButton(yesButton);
        window.addButton(noButton);
    }

    /**
     * Cập nhật trạng thái Xác nhận Thoát.
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
        // No physics or updates while paused
        window.update(deltaTime);
    }

    /**
     * Vẽ (render) trạng thái Xác nhận Thoát.
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code window.render()}.
     * Nếu transition hoàn tất, vẽ văn bản cảnh báo ("WARNING")
     * và thông điệp ("...will not be saved").
     * <p>
     * <b>Expected:</b> Cửa sổ xác nhận
     * (bao gồm nền mờ, văn bản, và các nút) được vẽ lên canvas.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        window.render(gc);

        if (window.transitionFinished()) {
            // Title text (centered, outlined, shadow)
            gc.setTextAlign(TextAlignment.CENTER);
            DropShadow titleShadow = new DropShadow(10, Color.color(0, 0, 0, 0.7));
            TextRenderer.drawOutlinedText(
                    gc,
                    "WARNING",
                    centerX,
                    window.getY() + 60,
                    warningFont,
                    Color.RED,
                    Color.color(0, 0, 0, 0.9),
                    2.0,
                    titleShadow
            );
            TextRenderer.drawOutlinedText(
                    gc,
                    "Your progress on this level\nwill not be saved.",
                    centerX,
                    window.getY() + 98,
                    messageFont,
                    Color.WHITE,
                    Color.color(0, 0, 0, 0.9),
                    2.0,
                    titleShadow
            );
            TextRenderer.drawOutlinedText(
                    gc,
                    "Continue?",
                    centerX,
                    window.getY() + 180,
                    messageFont,
                    Color.YELLOW,
                    Color.color(0, 0, 0, 0.9),
                    2.0,
                    titleShadow
            );
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }


    /**
     * Xử lý input (click chuột) của người dùng.
     * <p>
     * <b>Định nghĩa:</b> Ủy quyền (delegate) xử lý input cho {@link Window}
     * (để cập nhật trạng thái nút). Kiểm tra click cho nút "Yes" và "No".
     * <p>
     * <b>Expected:</b> Nếu click "Yes", phát sự kiện
     * {@link ChangeStateEvent} (MAIN_MENU).
     * Nếu click "No", phát sự kiện {@link ChangeStateEvent} (PAUSED).
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        window.handleInput(inputProvider);

        // Handle button clicks
        if (yesButton != null && yesButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.MAIN_MENU)
            );
        } else if (noButton != null && noButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.PAUSED)
            );
        }
    }

    /**
     * Lấy về trạng thái game trước đó.
     * <p>
     * <b>Định nghĩa:</b> Trả về đối tượng GameState đã được lưu khi khởi tạo.
     * <p>
     * <b>Expected:</b> Trạng thái game (VD: PauseState)
     * mà từ đó cửa sổ xác nhận được mở.
     *
     * @return GameState Trạng thái game trước đó.
     */
    public GameState getPreviousState() {
        return previousState;
    }

    /**
     * Dọn dẹp (cleanup) trạng thái trước khi thoát.
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code cleanUp()} của {@code previousState}
     * nếu đó là {@code PlayingState}.
     * (Dùng khi thoát game từ menu pause).
     * <p>
     * <b>Expected:</b> {@code PlayingState} (nếu có) được dọn dẹp
     * (hủy đăng ký sự kiện).
     */
    public void cleanUp() {
        if (previousState instanceof PlayingState) {
            ((PlayingState) previousState).cleanUp();
        }
    }
}