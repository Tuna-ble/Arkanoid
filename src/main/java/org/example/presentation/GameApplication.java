package org.example.presentation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.data.FileLevelRepository;
import org.example.data.ILevelRepository;
import org.example.gamelogic.core.GameManager;

public class GameApplication extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public void start(Stage primaryStage) throws Exception {
        GameManager gameManager = GameManager.getInstance();
        ILevelRepository repo = new FileLevelRepository();

        gameManager.setLevelRepository(repo);
        gameManager.init();

        Canvas canvas = new Canvas((double) WIDTH, (double) HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, (double) WIDTH, (double) HEIGHT);

        primaryStage.setTitle("Arkanoid");
        primaryStage.setScene(scene);
        primaryStage.show();

        gameManager.setGraphicsContext(gc);

        addInputHandlers(scene, gameManager.getInputHandler());

        gameManager.startGameLoop();
    }

    private void addInputHandlers(Scene scene, InputHandler inputHandler) {
        scene.setOnKeyPressed(event -> {
            inputHandler.keyPressed(event.getCode());
        });
        scene.setOnKeyReleased(event -> {
            inputHandler.keyReleased(event.getCode());
        });
        scene.setOnMouseMoved(event -> {
            inputHandler.mouseMoved((int) event.getX(), (int) event.getY());
        });
        scene.setOnMousePressed(event -> {
            inputHandler.mousePressed((int) event.getX(), (int) event.getY());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
