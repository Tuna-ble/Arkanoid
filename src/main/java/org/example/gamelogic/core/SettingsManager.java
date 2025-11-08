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
    private double musicVolume;
    private double sfxVolume;
    private String selectedMusic;;

    private SettingsManager() {
        this.properties = new Properties();
        loadSettings();
    }

    public void loadSettings() {
        File settingsFile = new File(SETTINGS_FILE_PATH);

        if (!settingsFile.exists()) {
            this.sfxEnabled = true;
            this.musicEnabled = true;
            this.musicVolume = 1.0;
            this.sfxVolume = 1.0;
            this.selectedMusic = "default_music";
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

            this.selectedMusic = properties.getProperty("selectedMusic", "default_music");

        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi khi đọc file settings: " + e.getMessage());
            this.sfxEnabled = true;
            this.musicEnabled = true;
            this.musicVolume = 1.0;
            this.sfxVolume = 1.0;
            this.selectedMusic = "default_music";
        }
    }

    public void saveSettings() {
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

    public double getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(double musicVolume) {
        this.musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
    }

    public double getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(double sfxVolume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, sfxVolume));
    }
}
