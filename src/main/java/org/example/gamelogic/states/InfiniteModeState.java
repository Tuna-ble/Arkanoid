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
import org.example.gamelogic.core.ProgressManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ScrollDownTransitionStrategy;
import java.util.Map;

/**
 * Quản lý trạng thái "Chế độ Vô Tận" (Infinite Mode).
 * <p>
 * Lớp này hiển thị một cửa sổ (Window) cho phép người chơi
 * bắt đầu Chế độ Vô Tận. Nó kiểm tra xem có "session"
 * (wave đang chơi) hoặc "save" (lưu trong level) hay không
 * để hiển thị các tùy chọn "Continue", "New Game" hoặc "Back".
 */
public final class InfiniteModeState implements GameState {

    private final Image background;
    private final Window window;

    private final AbstractButton banner;
    private final AbstractButton infoPanel;
    private AbstractButton startButton;
    private AbstractButton newGameButton;
    private final AbstractButton backButton;

    private final Font titleFont;
    private final Font infoFont;

    private boolean hasInLevelSave = false;
    private int savedWave = 1;
    private boolean hasSession = false;

    /**
     * Khởi tạo trạng thái Chế độ Vô Tận.
     * <p>
     * <b>Định nghĩa:</b> Tải tài nguyên (ảnh, font).
     * Tải (load) dữ liệu {@link ProgressManager} và
     * {@link SaveGameRepository} để kiểm tra trạng thái lưu.
     * Khởi tạo {@link Window} và các nút bấm
     * (Continue/New Game/Back)
     * dựa trên việc có tồn tại save hay không.
     * <p>
     * <b>Expected:</b> Trạng thái sẵn sàng update/render,
     * các nút bấm được hiển thị chính xác
     * theo dữ liệu save của người chơi.
     */
    public InfiniteModeState() {
        AssetManager am = AssetManager.getInstance();

        this.titleFont = am.getFont("Anxel", 70);
        this.infoFont = am.getFont("Anxel", 30);
        this.background = am.getImage("infiniteBackground");

        Image bannerImage = am.getImage("banner2");
        Image panelImage = am.getImage("page1");
        Image normalImage = am.getImage("selectButton");
        Image hoverImage = am.getImage("selectButtonHovered");

        Map<String, String> data = ProgressManager.loadSession("INFINITE");
        this.hasSession = !data.isEmpty();
        if (this.hasSession) {
            this.savedWave = Integer.parseInt(data.get("level"));
        } else {
            this.savedWave = 1;
        }

        SaveGameRepository repo = new SaveGameRepository();
        this.hasInLevelSave = repo.hasSave(this.savedWave);

        ITransitionStrategy transition = new ScrollDownTransitionStrategy();
        this.window = new Window(null, 850, 650, transition);

        double bannerX = window.getX() + window.getWidth() / 2.0 - GameConstants.UI_BANNER_WIDTH / 2.0;
        double bannerY = window.getY() + GameConstants.UI_BUTTON_PADDING + 10;
        this.banner = new Button(bannerX, bannerY, GameConstants.UI_BANNER_WIDTH, GameConstants.UI_BANNER_HEIGHT,
                bannerImage, bannerImage, "INFINITE");
        this.banner.setTransition(new WipeElementTransitionStrategy(0.5));

        double panelX = window.getX() + (window.getWidth() - 500) / 2.0;
        double panelY = 220;
        this.infoPanel = new Button(panelX, panelY, 500, 300,
                panelImage, panelImage, "");
        this.infoPanel.setTransition(new WipeElementTransitionStrategy(0.5));

        double buttonY = panelY + 300 + 60;

        if (this.hasInLevelSave) {

            this.startButton = new Button(panelX, buttonY, 150, 60,
                    normalImage, hoverImage, "Continue");

            this.newGameButton = new Button(panelX + 175, buttonY, 150, 60,
                    normalImage, hoverImage, "New Game");

            this.backButton = new Button(panelX + 350, buttonY, 150, 60,
                    normalImage, hoverImage, "Back");

        } else {
            String startText = "New Game";
            if (this.hasSession) {
                startText = "Start Wave " + this.savedWave;
            }

            this.startButton = new Button(panelX, buttonY, 200, 60,
                    normalImage, hoverImage, startText);

            this.backButton = new Button(panelX + 300, buttonY, 200, 60,
                    normalImage, hoverImage, "Back");

            this.newGameButton = null;
        }

        this.startButton.setTransition(new WipeElementTransitionStrategy(0.5));
        this.backButton.setTransition(new WipeElementTransitionStrategy(0.5));
        if (this.newGameButton != null) {
            this.newGameButton.setTransition(new WipeElementTransitionStrategy(0.5));
        }

        window.addButton(banner);
        window.addButton(infoPanel);
        window.addButton(startButton);
        window.addButton(backButton);
        if (newGameButton != null) {
            window.addButton(newGameButton);
        }
    }

    /**
     * Cập nhật trạng thái.
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
     * Vẽ (render) trạng thái Chế độ Vô Tận.
     * <p>
     * <b>Định nghĩa:</b> Vẽ ảnh nền, sau đó gọi {@code window.render()}.
     * Nếu transition hoàn tất, vẽ văn bản thông tin
     * (ví dụ: "Continue your progress...")
     * lên trên panel thông tin.
     * <p>
     * <b>Expected:</b> Giao diện Chế độ Vô Tận được hiển thị đầy đủ.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(background, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        window.render(gc);

        if (window.transitionFinished()) {
            double panelX = infoPanel.getX();
            double panelY = infoPanel.getY();
            double panelWidth = infoPanel.getWidth();
            double panelHeight = infoPanel.getHeight();
            double centerX = panelX + panelWidth / 2;
            double centerY = panelY + panelHeight / 2;

            gc.setTextAlign(TextAlignment.CENTER);

            if (this.hasInLevelSave) {
                TextRenderer.drawOutlinedText(gc, "Continue your progress from",
                        centerX, centerY - 30,
                        infoFont, Color.WHITE, Color.BLACK, 1.0, null);
                TextRenderer.drawOutlinedText(gc, "Wave " + this.savedWave,
                        centerX, centerY + 30,
                        infoFont, Color.LIMEGREEN, Color.BLACK, 1.0, null);
            } else {
                TextRenderer.drawOutlinedText(gc, "Song dai thanh huyen thoai.",
                        centerX, centerY - 30,
                        infoFont, Color.WHITE, Color.BLACK, 1.0, null);
                TextRenderer.drawOutlinedText(gc, "Good luck!",
                        centerX, centerY + 30,
                        infoFont, Color.LIMEGREEN, Color.BLACK, 1.0, null);
            }
        }
    }

    /**
     * Xử lý input (click chuột) của người dùng.
     * <p>
     * <b>Định nghĩa:</b> Ủy quyền (delegate) xử lý input cho {@link Window}.
     * Kiểm tra click cho các nút ("Start", "New Game", "Back").
     * <p>
     * <b>Expected:</b> Phát sự kiện {@link ChangeStateEvent} (GAME_MODE)
     * khi click "Back".
     * Tải game (nếu có save) hoặc
     * bắt đầu game mới (gọi {@link #startSession(boolean)})
     * khi click "Start" hoặc "New Game".
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        window.handleInput(inputProvider);

        if (startButton.isClicked()) {
            GameManager gm = GameManager.getInstance();

            if (this.hasInLevelSave) {
                SaveGameRepository repo = new SaveGameRepository();
                SavedGameState savedData = repo.loadGame(this.savedWave);

                if (savedData != null) {
                    repo.deleteSave(this.savedWave);
                    GameModeEnum mode = gm.getCurrentGameMode();

                    PlayingState playingState = new PlayingState(
                            gm, mode, this.savedWave, false
                    );
                    playingState.loadGame(savedData);

                    gm.getStateManager().setState(playingState);
                } else {
                    startSession(false);
                }
            } else {
                startSession(false);
            }
        }

        if (newGameButton != null && newGameButton.isClicked()) {
            startSession(true);
        }

        if (backButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.GAME_MODE)
            );
        }
    }

    /**
     * (Helper) Bắt đầu một phiên (session) chơi mới.
     * <p>
     * <b>Định nghĩa:</b> Xóa session và save cũ
     * nếu {@code reset} là true.
     * Phát sự kiện để chuyển sang trạng thái {@code PLAYING}.
     * <p>
     * <b>Expected:</b> Phát sự kiện {@link ChangeStateEvent}
     * (PLAYING). Dữ liệu cũ bị xóa nếu {@code reset} là true.
     *
     * @param reset True nếu muốn xóa toàn bộ tiến trình
     * (session và save) trước khi bắt đầu.
     */
    private void startSession(boolean reset) {
        if (reset) {
            ProgressManager.clearSession("INFINITE");
            SaveGameRepository repo = new SaveGameRepository();
            repo.deleteSave(this.savedWave);
        }

        EventManager.getInstance().publish(
                new ChangeStateEvent(GameStateEnum.PLAYING)
        );
    }
}