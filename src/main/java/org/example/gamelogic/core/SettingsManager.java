package org.example.gamelogic.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public final class SettingsManager {
    private static class SingletonHolder {
        private static final SettingsManager INSTANCE = new SettingsManager();
    }

    public static SettingsManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final String SETTINGS_FILE_PATH =
            System.getProperty("user.home") + File.separator + ".arkanoid_settings.properties";

    private Properties properties;

    private boolean sfxEnabled;
    private boolean musicEnabled;
    private String selectedMusic;;

    private SettingsManager() {
        this.properties = new Properties();
        loadSettings();
    }

    public void loadSettings() {
        File settingsFile = new File(SETTINGS_FILE_PATH);

        // Nếu file không tồn tại, dùng giá trị mặc định
        if (!settingsFile.exists()) {
            this.sfxEnabled = true;
            this.musicEnabled = true;
            // Lưu file mặc định lần đầu
            saveSettings();
            return;
        }

        // Tải
        try (FileReader reader = new FileReader(settingsFile)) {
            properties.load(reader);

            // Đọc giá trị, nếu không thấy key thì dùng "true" làm mặc định
            this.sfxEnabled = Boolean.parseBoolean(properties.getProperty("sfxEnabled", "true"));
            this.musicEnabled = Boolean.parseBoolean(properties.getProperty("musicEnabled", "true"));

        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file settings: " + e.getMessage());
            // Lỗi thì dùng mặc định
            this.sfxEnabled = true;
            this.musicEnabled = true;
        }
    }

    public void saveSettings() {
        try (FileWriter writer = new FileWriter(SETTINGS_FILE_PATH)) {
            // Cập nhật properties từ biến hiện tại
            properties.setProperty("sfxEnabled", String.valueOf(this.sfxEnabled));
            properties.setProperty("musicEnabled", String.valueOf(this.musicEnabled));

            // Ghi ra file
            properties.store(writer, "Arkanoid Game Settings");

        } catch (IOException e) {
            System.err.println("Lỗi khi lưu file settings: " + e.getMessage());
        }
    }

    public boolean isSfxEnabled() {
        return this.sfxEnabled;
    }

    public boolean isMusicEnabled() {
        return this.musicEnabled;
    }

    public String getSelectedMusic() {
        return this.selectedMusic;
    }

    public void setSelectedMusic(String selectedMusic) {
        this.selectedMusic = selectedMusic;
        properties.setProperty("selectedMusic", selectedMusic);
    }

    public void setSfxEnabled(boolean sfxEnabled) {
        this.sfxEnabled = sfxEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public void toggleSfx() {
        setSfxEnabled(!this.sfxEnabled);
    }

    public void toggleMusic() {
        setMusicEnabled(!this.musicEnabled);
    }
}
