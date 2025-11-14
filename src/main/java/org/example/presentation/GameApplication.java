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


public class GameApplication extends Application {

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

    @Override
    public void stop() throws Exception {
        System.out.println("Đang tắt ứng dụng và thực hiện lưu game...");

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

            System.out.println("Đã tự động lưu game cho level " + levelId);
        } else {
            System.out.println("Không ở trong PlayingState, không cần lưu.");
        }

        gameManager.stopGameLoop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}