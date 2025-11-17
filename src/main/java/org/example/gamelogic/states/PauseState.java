package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.PopupTransitionStrategy;

/**
 * Quản lý trạng thái "Tạm dừng" (Pause) của game.
 * <p>
 * Lớp này hiển thị một cửa sổ (Window) với các tùy chọn
 * (Resume, Settings, Quit) và xử lý input cho chúng.
 */
public final class PauseState implements GameState {
    private final GameState previousState;
    private final Window window;

    private final Image normalImage;
    private final Image hoveredImage;
    private final Image bannerImage;
    // Button layout uses GameConstants

    // Center screen position
    private double centerX, centerY;

    // Button instances
    private AbstractButton banner;
    private AbstractButton resumeButton;
    private AbstractButton settingsButton;
    private AbstractButton quitButton;

    /**
     * Khởi tạo trạng thái Tạm dừng.
     * <p>
     * <b>Định nghĩa:</b> Lưu trạng thái game trước đó ({@code previousState}).
     * Khởi tạo {@link Window} với hiệu ứng transition.
     * Tải tài nguyên và tạo các nút (Resume, Settings, Quit, Banner).
     * <p>
     * <b>Expected:</b> Cửa sổ Tạm dừng được tạo,
     * chứa các nút, và sẵn sàng cho việc update/render.
     *
     * @param previousState Trạng thái game ngay trước khi tạm dừng
     * (thường là {@link PlayingState}).
     */
    public PauseState(GameState previousState) {
        this.previousState = previousState;

        ITransitionStrategy transition = new PopupTransitionStrategy();
        this.window = new Window(previousState, 300, 400, transition);

        AssetManager am = AssetManager.getInstance();
        this.normalImage = am.getImage("selectButton");
        this.hoveredImage = am.getImage("selectButtonHovered");
        this.bannerImage = am.getImage("banner2");

        centerX = GameConstants.SCREEN_WIDTH / 2;
        centerY = GameConstants.SCREEN_HEIGHT / 2;
        double bannerX = centerX - GameConstants.UI_BANNER_WIDTH / 2;
        double buttonX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        double bannerY = window.getY() + GameConstants.UI_BUTTON_PADDING;
        double resumeY = bannerY + GameConstants.UI_BANNER_HEIGHT + GameConstants.UI_BUTTON_PADDING + 20;
        double settingsY = resumeY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING;
        double quitY = settingsY + GameConstants.UI_BUTTON_HEIGHT + GameConstants.UI_BUTTON_SPACING;
        banner = new Button(bannerX, bannerY,
                GameConstants.UI_BANNER_WIDTH, GameConstants.UI_BANNER_HEIGHT, bannerImage, bannerImage, "PAUSED");
        resumeButton = new Button(buttonX, resumeY, normalImage, hoveredImage, "Resume");
        quitButton = new Button(buttonX, quitY, normalImage, hoveredImage, "Quit");
        settingsButton = new Button(buttonX, settingsY, normalImage, hoveredImage, "Settings");

        window.addButton(banner);
        window.addButton(resumeButton);
        window.addButton(settingsButton);
        window.addButton(quitButton);
    }

    /**
     * Cập nhật trạng thái Tạm dừng.
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
     * Vẽ (render) trạng thái Tạm dừng.
     * <p>
     * <b>Định nghĩa:</b> Xóa màn hình và ủy quyền (delegate)
     * logic vẽ cho {@link Window}.
     * <p>
     * <b>Expected:</b> Cửa sổ Tạm dừng (bao gồm nền mờ
     * và các nút) được vẽ lên canvas.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.setTextAlign(TextAlignment.LEFT);
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        window.render(gc);
        gc.setTextAlign(TextAlignment.LEFT);
    }


    /**
     * Xử lý input (click chuột) của người dùng.
     * <p>
     * <b>Định nghĩa:</b> Ủy quyền (delegate) xử lý input cho
     * {@link Window} (để cập nhật trạng thái nút).
     * Kiểm tra click cho từng nút.
     * <p>
     * <b>Expected:</b> Phát sự kiện {@link ChangeStateEvent}
     * (RESUME_GAME, SETTINGS, CONFIRM_QUIT_TO_MENU)
     * khi nút tương ứng được click.
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;

        window.handleInput(inputProvider);

        // Handle button clicks
        if (resumeButton != null && resumeButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.RESUME_GAME)
            );
        } else if (settingsButton != null && settingsButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.SETTINGS)
            );
        } else if (quitButton != null && quitButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.CONFIRM_QUIT_TO_MENU)
            );
        }
    }

    /**
     * Lấy về trạng thái game trước đó.
     * <p>
     * <b>Định nghĩa:</b> Trả về đối tượng GameState đã được lưu khi khởi tạo.
     * <p>
     * <b>Expected:</b> Trạng thái game (VD: PlayingState)
     * mà từ đó Pause được mở.
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