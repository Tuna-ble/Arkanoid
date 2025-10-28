package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.example.config.GameConstants;
import org.example.gamelogic.core.GameManager;
import org.example.presentation.InputHandler;

import java.io.File;

public final class MainMenuState implements GameState {
    private final GameManager gameManager;
    private MediaPlayer mediaPlayer;
    private double elapsedTime = 0;
    private boolean videoLoaded = false;

    public MainMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
        initializeVideo();
    }

    private void initializeVideo() {
        try {
            String videoPath = new File("src/main/resources/menu_video.mp4").toURI().toString();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0);
            mediaPlayer.play();
            videoLoaded = true;
        } catch (Exception e) {
            System.out.println("Video not found, using fallback background");
            videoLoaded = false;
        }
    }

    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw background
        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setFill(Color.color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        // Draw title
        gc.setFill(Color.web("#00ffff"));
        gc.setFont(new Font("Arial", 60));
        gc.fillText("ARKANOID", GameConstants.SCREEN_WIDTH / 2.0 - 150, 250);

        if ((int)(elapsedTime * 2) % 2 == 0) {
            gc.setFill(Color.web("#ffffff"));
            gc.setFont(new Font("Arial", 15));
            gc.fillText("Press SPACE to Start", GameConstants.SCREEN_WIDTH / 2.0 - 60, GameConstants.SCREEN_HEIGHT / 2.0);
        }
    }

    @Override
    public void handleInput(InputHandler inputHandler) {
        if (inputHandler == null) return;
        if (inputHandler.isKeyPressed(KeyCode.ESCAPE)) {
            System.exit(0);
        }
        if (inputHandler.isKeyPressed(KeyCode.SPACE)) {
            startGame();
        }
    }

    private void startGame() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        GameState playingState = new PlayingState(gameManager, 1);
        gameManager.setState(playingState);
    }
}
