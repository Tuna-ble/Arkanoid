package org.example.presentation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.data.FileLevelRepository;
import org.example.data.ILevelRepository;
import org.example.gamelogic.core.GameManager;
import org.example.config.GameConstants;


public class GameApplication extends Application {

    public void start(Stage primaryStage) throws Exception {
        GameManager gameManager = GameManager.getInstance();
        ILevelRepository repo = new FileLevelRepository();

        InputHandler inputHandler = new InputHandler();

        gameManager.setLevelRepository(repo);
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

        scene.setOnMouseClicked(event -> {
            inputHandler.setMouseClicked(true);
        });

        primaryStage.setTitle("Arkanoid");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        gameManager.setGraphicsContext(gc);
        gameManager.startGameLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}