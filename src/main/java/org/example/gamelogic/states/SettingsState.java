package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.SettingsManager;
import org.example.gamelogic.core.SoundManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class SettingsState implements GameState {
    private GameState previousState;
    private Button musicButton;
    private Button sfxButton;
    private Button backButton;
    private Button musicVolumeDown;
    private Button musicVolumeUp;
    private Button sfxVolumeDown;
    private Button sfxVolumeUp;
    private Button prevMusicButton;
    private Button nextMusicButton;

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private Image settingsImage = null;

    private final String[] musicTracks = {"default_music"};
    private int currentMusicIndex = 0;

    public SettingsState(GameState previousState) {
        this.previousState = previousState;
        double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        boolean musicOn = SettingsManager.getInstance().isMusicEnabled();
        boolean sfxOn = SettingsManager.getInstance().isSfxEnabled();

        this.musicButton = new Button(btnX, 200, "Music: " + (musicOn ? "ON" : "OFF"));
        this.sfxButton = new Button(btnX, 380, "SFX: " + (sfxOn ? "ON" : "OFF"));
        this.backButton = new Button(btnX, 510, "Back");

        double smallBtnWidth = 50;
        double sliderWidth = 220;
        double sliderX = centerX - sliderWidth / 2;

        this.musicVolumeDown = new Button(sliderX, 270, smallBtnWidth, 40, "-");
        this.musicVolumeUp = new Button(sliderX + sliderWidth
                - smallBtnWidth, 270, smallBtnWidth, 40, "+");

        this.sfxVolumeDown = new Button(sliderX, 450, smallBtnWidth, 40, "-");
        this.sfxVolumeUp = new Button(sliderX + sliderWidth
                - smallBtnWidth, 450, smallBtnWidth, 40, "+");

        this.prevMusicButton = new Button(sliderX, 320, smallBtnWidth, 40, "<");
        this.nextMusicButton = new Button(sliderX + sliderWidth
                - smallBtnWidth, 320, smallBtnWidth, 40, ">");

        String currentMusic = SettingsManager.getInstance().getSelectedMusic();
        for (int i = 0; i < musicTracks.length; i++) {
            if (musicTracks[i].equals(currentMusic)) {
                this.currentMusicIndex = i;
                break;
            }
        }

        org.example.data.AssetManager am = org.example.data.AssetManager.getInstance();
        this.settingsImage = am.getImage("settings");
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        SettingsManager settings = SettingsManager.getInstance();

        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        if (settingsImage != null) {
            gc.drawImage(settingsImage, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        }
        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc, "SETTINGS", centerX, 110,
                new Font("Arial", 70), Color.WHITE, Color.BLACK, 2.0, null
        );

        boolean musicOn = settings.isMusicEnabled();
        boolean sfxOn = settings.isSfxEnabled();
        musicButton.setText("Music: " + (musicOn ? "ON" : "OFF"));
        sfxButton.setText("SFX: " + (sfxOn ? "ON" : "OFF"));

        musicButton.render(gc);
        sfxButton.render(gc);
        backButton.render(gc);
        musicVolumeDown.render(gc);
        musicVolumeUp.render(gc);
        sfxVolumeDown.render(gc);
        sfxVolumeUp.render(gc);
        prevMusicButton.render(gc);
        nextMusicButton.render(gc);

        double sliderWidth = 100; // Chiều rộng thanh (220 - 50 - 50 - 20)
        double musicSliderX = musicVolumeDown.getX() + musicVolumeDown.getWidth() + 10;
        double sfxSliderX = sfxVolumeDown.getX() + sfxVolumeDown.getWidth() + 10;

        renderSlider(gc, musicSliderX, 275, sliderWidth, 30, settings.getMusicVolume());

        renderSlider(gc, sfxSliderX, 455, sliderWidth, 30, settings.getSfxVolume());

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(settings.getSelectedMusic(), centerX, 345);
    }

    private void renderSlider(GraphicsContext gc, double x, double y, double width, double height, double percent) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(x, y, width, height, 10, 10);

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRoundRect(x, y, width * percent, height, 10, 10);

        gc.setStroke(Color.WHITE);
        gc.strokeRoundRect(x, y, width, height, 10, 10);
    }

    @Override
    public void handleInput(I_InputProvider input) {
        if (input == null) return;

        musicButton.update(input);
        sfxButton.update(input);
        backButton.update(input);
        musicVolumeDown.update(input);
        musicVolumeUp.update(input);
        sfxVolumeDown.update(input);
        sfxVolumeUp.update(input);
        prevMusicButton.update(input);
        nextMusicButton.update(input);

        SettingsManager settings = SettingsManager.getInstance();

        if (musicButton.isClicked()) {
            settings.toggleMusic();
        }

        if (sfxButton.isClicked()) {
            settings.toggleSfx();
        }

        boolean volumeChanged = false;
        if (musicVolumeDown.isClicked()) {
            settings.setMusicVolume(settings.getMusicVolume() - 0.1);
            volumeChanged = true;
        }
        if (musicVolumeUp.isClicked()) {
            settings.setMusicVolume(settings.getMusicVolume() + 0.1);
            volumeChanged = true;
        }
        if (sfxVolumeDown.isClicked()) {
            settings.setSfxVolume(settings.getSfxVolume() - 0.1);
        }
        if (sfxVolumeUp.isClicked()) {
            settings.setSfxVolume(settings.getSfxVolume() + 0.1);
        }

        if (prevMusicButton.isClicked()) {
            currentMusicIndex = (currentMusicIndex - 1 + musicTracks.length) % musicTracks.length;
            settings.setSelectedMusic(musicTracks[currentMusicIndex]);
            volumeChanged = true;
        }
        if (nextMusicButton.isClicked()) {
            currentMusicIndex = (currentMusicIndex + 1) % musicTracks.length;
            settings.setSelectedMusic(musicTracks[currentMusicIndex]);
            volumeChanged = true;
        }

        if (volumeChanged || musicButton.isClicked()) {
            SoundManager.getInstance().updateAllVolumes();
        }

        if (backButton.isClicked()) {
            settings.saveSettings();
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.RESUME_GAME)
            );
        }
    }

    public GameState getPreviousState() {
        return this.previousState;
    }
}
