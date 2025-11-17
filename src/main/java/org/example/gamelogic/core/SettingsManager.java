package org.example.gamelogic.core;

import org.example.gamelogic.multithreading.AsyncExecutor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Quản lý thiết lập trò chơi (settings) bao gồm SFX, music và volume.
 *
 * <p>Lưu/ tải cấu hình vào file properties trong thư mục home. Sử dụng singleton.
 */
public final class SettingsManager {
    private static class SingletonHolder {
        private static final SettingsManager INSTANCE = new SettingsManager();
    }

    /**
     * Lấy instance đơn của SettingsManager.
     *
     * @return singleton SettingsManager
     */
    public static SettingsManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final String SETTINGS_FILE_PATH =
            System.getProperty("user.home") + File.separator + ".arkanoid_settings.properties";

    private Properties properties;

    private boolean sfxEnabled;
    private boolean musicEnabled;
    private double musicVolume;
    private double sfxVolume;
    private String selectedMusic;;

    private SettingsManager() {
        this.properties = new Properties();
        loadSettings();
    }

    /**
     * Tải settings từ file. Nếu file không tồn tại sẽ thiết lập mặc định và lưu lại.
     */
    public void loadSettings() {
        File settingsFile = new File(SETTINGS_FILE_PATH);

        if (!settingsFile.exists()) {
            this.sfxEnabled = true;
            this.musicEnabled = true;
            this.musicVolume = 1.0;
            this.sfxVolume = 1.0;
            this.selectedMusic = "music_1";
            saveSettings();
            return;
        }

        // Tải
        try (FileReader reader = new FileReader(settingsFile)) {
            properties.load(reader);

            this.sfxEnabled = Boolean.parseBoolean(properties.getProperty("sfxEnabled", "true"));
            this.musicEnabled = Boolean.parseBoolean(properties.getProperty("musicEnabled", "true"));

            this.musicVolume = Double.parseDouble(properties.getProperty("musicVolume", "0.8"));
            this.sfxVolume = Double.parseDouble(properties.getProperty("sfxVolume", "1.0"));

            this.selectedMusic = properties.getProperty("selectedMusic", "music_1");

        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi khi đọc file settings: " + e.getMessage());
            this.sfxEnabled = true;
            this.musicEnabled = true;
            this.musicVolume = 1.0;
            this.sfxVolume = 1.0;
            this.selectedMusic = "music_1";
        }
    }

    /**
     * Lưu settings bất đồng bộ vào file.
     */
    public void saveSettings() {
        AsyncExecutor.runAsync(() -> {
            try (FileWriter writer = new FileWriter(SETTINGS_FILE_PATH)) {
                properties.setProperty("sfxEnabled", String.valueOf(this.sfxEnabled));
                properties.setProperty("musicEnabled", String.valueOf(this.musicEnabled));

                properties.setProperty("musicVolume", String.valueOf(this.musicVolume));
                properties.setProperty("sfxVolume", String.valueOf(this.sfxVolume));

                properties.setProperty("selectedMusic", this.selectedMusic);

                // Ghi ra file
                properties.store(writer, "Arkanoid Game Settings");

            } catch (IOException e) {
                System.err.println("Lỗi khi lưu file settings: " + e.getMessage());
            }
        });
    }

    /**
     * Kiểm tra SFX có đang bật không.
     *
     * @return true nếu SFX được bật
     */
    public boolean isSfxEnabled() {
        return this.sfxEnabled;
    }

    /**
     * Kiểm tra music có đang bật không.
     *
     * @return true nếu music được bật
     */
    public boolean isMusicEnabled() {
        return this.musicEnabled;
    }

    /**
     * Lấy tên track music đang được chọn.
     *
     * @return tên music đã chọn
     */
    public String getSelectedMusic() {
        return this.selectedMusic;
    }

    /**
     * Chọn track music và cập nhật property tương ứng.
     *
     * @param selectedMusic tên music để chọn
     */
    public void setSelectedMusic(String selectedMusic) {
        this.selectedMusic = selectedMusic;
        properties.setProperty("selectedMusic", selectedMusic);
    }

    /**
     * Bật/tắt SFX.
     *
     * @param sfxEnabled true để bật SFX
     */
    public void setSfxEnabled(boolean sfxEnabled) {
        this.sfxEnabled = sfxEnabled;
    }

    /**
     * Bật/tắt music chung của game.
     *
     * @param musicEnabled true để bật music
     */
    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    /**
     * Chuyển trạng thái SFX (on/off).
     */
    public void toggleSfx() {
        setSfxEnabled(!this.sfxEnabled);
    }

    /**
     * Chuyển trạng thái music (on/off).
     */
    public void toggleMusic() {
        setMusicEnabled(!this.musicEnabled);
    }

    /**
     * Lấy volume music (0.0 - 1.0).
     *
     * @return giá trị volume music
     */
    public double getMusicVolume() {
        return musicVolume;
    }

    /**
     * Thiết lập volume music (giá trị sẽ được clamp trong [0.0,1.0]).
     *
     * @param musicVolume giá trị volume mong muốn
     */
    public void setMusicVolume(double musicVolume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
    }

    /**
     * Lấy volume SFX (0.0 - 1.0).
     *
     * @return giá trị volume SFX
     */
    public double getSfxVolume() {
        return sfxVolume;
    }

    /**
     * Thiết lập volume SFX (giá trị sẽ được clamp trong [0.0,1.0]).
     *
     * @param sfxVolume giá trị volume mong muốn
     */
    public void setSfxVolume(double sfxVolume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, sfxVolume));
    }
}
