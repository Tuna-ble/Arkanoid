package org.example.view;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import org.example.data.GameConstants;
import javafx.animation.AnimationTimer;
import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.Paddle;

public class GameView extends Application {

    private Paddle paddle;
    private Ball ball;
    private Canvas canvas;

    @Override
    public void start(Stage stage) {
        // Khởi tạo Canvas
        canvas = new Canvas(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Khởi tạo Paddle và Ball
        paddle = new Paddle(
                GameConstants.PADDLE_X,
                GameConstants.PADDLE_Y,
                GameConstants.PADDLE_WIDTH,
                GameConstants.PADDLE_HEIGHT,
                GameConstants.PADDLE_SPEED,
                0
        );

        ball = new Ball(
                GameConstants.BALL_X - 4,
                GameConstants.BALL_Y,
                GameConstants.BALL_RADIUS * 2,
                GameConstants.BALL_RADIUS * 2,
                GameConstants.BALL_INITIAL_SPEED,
                GameConstants.BALL_INITIAL_SPEED
        );

        // Cấu hình Scene
        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        stage.setTitle("Ball and Paddle Render Test");
        stage.setScene(scene);
        stage.show();

        // AnimationTimer để render liên tục
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                render(gc);
            }
        }.start();
    }

    private void render(GraphicsContext gc) {
        // Xóa nền
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        // Gọi hàm render của các đối tượng
        paddle.render(gc);
        ball.render(gc);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
