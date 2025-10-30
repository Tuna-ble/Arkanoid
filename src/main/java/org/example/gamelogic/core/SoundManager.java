package org.example.gamelogic.core;

import org.example.data.AssetManager;
import org.example.gamelogic.events.*;
import javax.sound.sampled.Clip;

public class SoundManager {
    private AssetManager assetManager;

    private static class SingletonHolder {
        private static final SoundManager INSTANCE = new SoundManager();
    }

    public static SoundManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private SoundManager() {
        this.assetManager = AssetManager.getInstance();

        subscribeToEvents();
    }

    public void subscribeToEvents() {
        EventManager.getInstance().subscribe(
                BrickDestroyedEvent.class,
                this::onBrickDestroyed
        );
        EventManager.getInstance().subscribe(
                BallHitPaddleEvent.class,
                this::onPaddleHit
        );
        EventManager.getInstance().subscribe(
                BallLostEvent.class,
                this::onBallLost
        );
        EventManager.getInstance().subscribe(
                GameOverEvent.class,
                this::onGameOver
        );
    }

    private void playSound(String name) {
        Clip clip = assetManager.getSound(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void loopMusic(String name) {
        Clip clip = assetManager.getSound(name);
        if (clip != null) {
            if (!clip.isRunning()) {
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public void onBrickDestroyed(BrickDestroyedEvent event) {
        playSound("brick_hit");
    }

    public void onPaddleHit(BallHitPaddleEvent event) {
        playSound("paddle_hit");
    }

    public void onBallLost(BallLostEvent event) {
        playSound("ball_lost");
    }

    public void onGameOver(GameOverEvent event) {
        // playSound("game_over");
        // Dừng nhạc nền (neu co)
        // stopSound
    }
}