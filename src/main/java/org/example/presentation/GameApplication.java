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
 * Entry point JavaFX cho game Arkanoid.
 * <br>Khởi tạo window, input handler và game loop.
 */
public class GameApplication extends Application {

    /**
     * Khởi tạo và hiển thị cửa sổ game, setup input và start game loop.
     *
     * @param primaryStage stage chính do JavaFX cung cấp
     * @throws Exception nếu có lỗi trong quá trình khởi tạo
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

        scene.setOnKeyPressed(event -> {
            inputHandler.addKey(event.getCode());
        });

        scene.setOnKeyReleased(event -> {
            inputHandler.removeKey(event.getCode());
        });

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
     * Được gọi khi ứng dụng đóng lại.
     * <br>Nếu đang ở PlayingState thì tự động lưu game hiện tại và dừng game loop.
     *
     * @throws Exception nếu có lỗi khi shutdown
     */
    @Override
    public void stop() throws Exception {
        System.out.println("Closing the app and saving...");

        GameManager gameManager = GameManager.getInstance();

        GameState currentState = null;
        if (gameManager.getStateManager() != null) {
            currentState = gameManager.getStateManager().getState();
        }

        if (currentState instanceof PlayingState) {
            PlayingState playingState = (PlayingState) currentState;

            SavedGameState dataToSave = playingState.collectGameStateToSave();
            int levelId = playingState.getLevelNumber();
            SaveGameRepository repo = new SaveGameRepository();
            repo.saveGame(dataToSave, levelId);

            System.out.println("Automatically saved for level " + levelId);
        } else {
            System.out.println("Not in PlayingState, no need to Le Duc Luu.");
        }

        gameManager.stopGameLoop();
        super.stop();
    }

    /**
     * Hàm main khởi chạy ứng dụng JavaFX.
     *
     * @param args tham số dòng lệnh (nếu có)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
