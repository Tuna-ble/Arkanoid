package org.example.gamelogic.core;

import org.example.data.AssetManager;
import org.example.gamelogic.events.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.bricks.ExplosiveBrick;
import org.example.gamelogic.entities.bricks.NormalBrick;
import org.example.gamelogic.entities.bricks.HardBrick;
import org.example.gamelogic.entities.bricks.HealingBrick;
import org.example.gamelogic.entities.bricks.UnbreakableBrick;
import org.example.gamelogic.events.PowerUpCollectedEvent;



/**
 * Quản lý âm thanh và nhạc nền của game: phát SFX, lặp nhạc, dừng nhạc và cập nhật volume.
 *
 * <p>Singleton; lắng nghe các sự kiện game để phát âm thanh phù hợp.
 */
public final class SoundManager {
    private AssetManager assetManager;
    private Clip currentMusicClip = null;

    private static class SingletonHolder {
        private static final SoundManager INSTANCE = new SoundManager();
    }

    /**
     * Lấy instance đơn của SoundManager.
     *
     * @return singleton SoundManager
     */
    public static SoundManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private SoundManager() {
        this.assetManager = AssetManager.getInstance();

        subscribeToEvents();
    }

    /**
     * Đăng ký các listener cho sự kiện liên quan tới âm thanh.
     */
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
                GameOverEvent.class,
                this::onGameOver
        );
        EventManager.getInstance().subscribe(
                LevelCompletedEvent.class,
                this::onLevelCompleted
        );
        EventManager.getInstance().subscribe(
                BallHitBrickEvent.class,
                this::onBallHitBrick
        );
        EventManager.getInstance().subscribe(
                PowerUpCollectedEvent.class,
                this::onPowerUpCollected
        );
    }

    private void setClipVolume(Clip clip, double volume) {
        if (clip == null) return;

        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(Math.max(volume, 0.0001)) * 20.0);

            dB = Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum()));

            gainControl.setValue(dB);
        } catch (Exception e) {
            System.err.println("Không thể set volume: " + e.getMessage());
        }
    }

    /**
     * Dừng một sound cụ thể nếu đang chạy.
     *
     * @param soundName tên asset âm thanh
     */
    private void stopSound(String soundName) {
        Clip clip = assetManager.getSound(soundName);
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }

    /**
     * Phát một hiệu ứng âm thanh (SFX) theo tên asset nếu SFX đang bật.
     *
     * @param name tên âm thanh (khớp với assets)
     */
    public void playSound(String name) {
        if (!SettingsManager.getInstance().isSfxEnabled()) return;

        Clip clip = assetManager.getSound(name);
        if (clip != null) {
            double sfxVolume = SettingsManager.getInstance().getSfxVolume();
            setClipVolume(clip, sfxVolume);
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Lặp nhạc nền theo tên asset (dừng nhạc trước khi lặp).
     *
     * @param name tên track nhạc
     */
    public void loopMusic(String name) {
        stopMusic();

        if (!SettingsManager.getInstance().isMusicEnabled()) return;

        Clip clip = assetManager.getSound(name);
        if (clip != null) {
            currentMusicClip = clip;
            double musicVolume = SettingsManager.getInstance().getMusicVolume();
            setClipVolume(currentMusicClip, musicVolume);

            currentMusicClip.setFramePosition(0);
            currentMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Phát track được chọn trong settings.
     */
    public void playSelectedMusic() {
        String musicName = SettingsManager.getInstance().getSelectedMusic();
        loopMusic(musicName);
    }

    /**
     * Dừng nhạc nền hiện tại (nếu có).
     */
    public void stopMusic() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
            currentMusicClip.setFramePosition(0);
        }
        currentMusicClip = null;
    }

    /**
     * Cập nhật volume cho music hiện tại theo settings và dừng/tiếp tục nếu cần.
     */
    public void updateAllVolumes() {
        double musicVolume = SettingsManager.getInstance().getMusicVolume();

        String musicName = SettingsManager.getInstance().getSelectedMusic();
        Clip musicClip = assetManager.getSound(musicName);
        if (musicClip != null) {
            if (SettingsManager.getInstance().isMusicEnabled()) {
                setClipVolume(musicClip, musicVolume);
            } else {
                musicClip.stop();
            }
        }
    }

    /**
     * Xử lý event khi gạch bị phá: phát hiệu ứng phù hợp.
     *
     * @param event sự kiện BrickDestroyedEvent
     */
    public void onBrickDestroyed(BrickDestroyedEvent event) {
        stopSound("brick_hit");
        playSound("brick_destroyed");
    }

    /**
     * Xử lý event khi paddle bị bóng chạm: phát SFX.
     *
     * @param event BallHitPaddleEvent
     */
    public void onPaddleHit(BallHitPaddleEvent event) {
        playSound("paddle_hit");
    }

    /**
     * Xử lý event khi game over: dừng nhạc và phát SFX mất bóng.
     *
     * @param event GameOverEvent
     */
    public void onGameOver(GameOverEvent event) {
        stopMusic();
        playSound("ball_lost");
    }

    /**
     * Xử lý event khi level hoàn thành: dừng nhạc và phát SFX chiến thắng.
     *
     * @param event LevelCompletedEvent
     */
    public void onLevelCompleted(LevelCompletedEvent event) {
        stopMusic();
        playSound("victory");
    }

    /**
     * Xử lý event khi một viên gạch bị bóng chạm/đập: phát SFX tương ứng theo loại gạch.
     *
     * @param event BallHitBrickEvent chứa viên gạch bị tác động
     */
    private void onBallHitBrick(BallHitBrickEvent event) {

        if (event == null || event.getBrick() == null) {
            return;
        }

        Brick brick = event.getBrick();

        if (brick instanceof UnbreakableBrick) {
            playSound("glass");
        }
        else if (brick instanceof ExplosiveBrick) {
            playSound("bomb");
        }
        else if (brick instanceof HardBrick || brick instanceof HealingBrick) {
            playSound("brick_hit");
        }

    }

    /**
     * Xử lý khi người chơi nhặt power-up: phát hiệu ứng âm thanh.
     *
     * @param event PowerUpCollectedEvent
     */
    private void onPowerUpCollected(PowerUpCollectedEvent event) {
        playSound("powerup");
    }
}
