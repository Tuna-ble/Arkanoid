package org.example.presentation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.config.GameConstants;
import org.example.data.FileLevelRepository;
import org.example.data.ILevelRepository;
import org.example.gamelogic.core.GameManager;
import org.example.data.SaveGameRepository;
import org.example.data.SavedGameState;
import org.example.gamelogic.states.GameState;
import org.example.gamelogic.states.PlayingState;

/**
 * Lớp chính của ứng dụng game (JavaFX Application).
 * Chịu trách nhiệm khởi tạo cửa sổ, xử lý input, và quản lý vòng đời ứng dụng.
 */
public class GameApplication extends Application {

    /**
     * Khởi tạo và bắt đầu ứng dụng game.
     * <p>
     * <b>Định nghĩa:</b> Cấu hình GameManager, InputHandler, Scene,
     * gắn các trình lắng nghe sự kiện (input) và hiển thị cửa sổ game (Stage).
     * Bắt đầu vòng lặp game.
     * <p>
     * <b>Expected:</b> Cửa sổ game "Arkanoid" hiển thị và game bắt đầu chạy,
     * sẵn sàng nhận input.
     *
     * @param primaryStage Stage chính (cửa sổ) của ứng dụng.
     * @throws Exception Lỗi nếu quá trình khởi tạo thất bại.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        GameManager gameManager = GameManager.getInstance();

        InputHandler inputHandler = new InputHandler();

        gameManager.setInputProvider(inputHandler);

        gameManager.init();

        Canvas canvas = new Canvas(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        // Đăng ký sự kiện bàn phím
        scene.setOnKeyPressed(event -> {
            inputHandler.addKey(event.getCode());
        });

        scene.setOnKeyReleased(event -> {
            inputHandler.removeKey(event.getCode());
        });

        // Đăng ký sự kiện chuột
        scene.setOnMouseMoved(event -> {
            inputHandler.setMousePos(event.getX(), event.getY());
        });

        scene.setOnMouseDragged(event -> {
            inputHandler.setMousePos(event.getX(), event.getY());
        });

        scene.setOnMouseClicked(event -> {
            inputHandler.setMouseClicked(true);
        });

        scene.setOnMousePressed(event -> {
            inputHandler.setMousePressed(true);
        });

        scene.setOnMouseReleased(event -> {
            inputHandler.setMouseReleased();
        });

        primaryStage.setTitle("Arkanoid");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        gameManager.setGraphicsContext(gc);
        gameManager.startGameLoop();
    }

    /**
     * Được gọi khi ứng dụng đóng.
     * <p>
     * <b>Định nghĩa:</b> Tự động lưu trạng thái game nếu đang chơi (PlayingState)
     * và dừng vòng lặp game một cách an toàn.
     * <p>
     * <b>Expected:</b> Game được lưu (nếu cần) và ứng dụng tắt an toàn.
     *
     * @throws Exception Lỗi nếu lưu game hoặc dừng ứng dụng thất bại.
     */
    @Override
    public void stop() throws Exception {
        System.out.println("Closing the app and saving...");

        GameManager gameManager = GameManager.getInstance();

        GameState currentState = null;
        if (gameManager.getStateManager() != null) {
            currentState = gameManager.getStateManager().getState();
        }

        // Chỉ lưu game nếu đang ở trạng thái chơi
        if (currentState instanceof PlayingState) {
            PlayingState playingState = (PlayingState) currentState;

            SavedGameState dataToSave = playingState.collectGameStateToSave();
            int levelId = playingState.getLevelNumber();
            SaveGameRepository repo = new SaveGameRepository();
            repo.saveGame(dataToSave, levelId);

            System.out.println("Automatically saved for level " + levelId);
        } else {
            System.out.println("Not in PlayingState, no need to save.");
        }

        // Dừng vòng lặp game trước khi thoát
        gameManager.stopGameLoop();
        super.stop();
    }

    /**
     * Phương thức main, khởi chạy ứng dụng JavaFX.
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code launch(args)} để bắt đầu JavaFX runtime.
     * <p>
     * <b>Expected:</b> JavaFX được khởi tạo và gọi phương thức {@code start()}.
     *
     * @param args Đối số dòng lệnh (không dùng trong ứng dụng này).
     */
    public static void main(String[] args) {
        launch(args);
    }
}