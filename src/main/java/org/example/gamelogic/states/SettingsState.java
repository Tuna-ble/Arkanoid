package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.SettingsManager;
import org.example.gamelogic.core.SoundManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.buttons.Slider;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.HologramTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;

public final class SettingsState implements GameState {
    private GameState previousState;
    private Window window;
    private AbstractButton musicButton;
    private AbstractButton sfxButton;
    private AbstractButton backButton;
    private AbstractButton prevMusicButton;
    private AbstractButton nextMusicButton;

    private Slider musicSlider;
    private Slider sfxSlider;

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private Image settingsImage = null;

    private final String[] musicTracks = {"music_1", "music_2", "music_3"};
    private int currentMusicIndex = 0;

    public SettingsState(GameState previousState) {
        this.previousState = previousState;

        ITransitionStrategy transition = new HologramTransitionStrategy();
        this.window = new Window(previousState, 500, 450, transition,
                "SETTINS", null);

        AssetManager am = AssetManager.getInstance();
        SettingsManager settings = SettingsManager.getInstance();

        double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        boolean musicOn = settings.isMusicEnabled();
        boolean sfxOn = settings.isSfxEnabled();

        final Image normalImage = am.getImage("button");
        final Image hoveredImage = am.getImage("hoveredButton");

        this.musicButton = new Button(btnX, 200, normalImage,
                hoveredImage, "Music: " + (musicOn ? "ON" : "OFF"));
        this.musicButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.sfxButton = new Button(btnX, 380, normalImage,
                hoveredImage, "SFX: " + (sfxOn ? "ON" : "OFF"));
        this.sfxButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.backButton = new Button(btnX, 510, normalImage,
                hoveredImage, "Back");
        this.backButton.setTransition(new WipeElementTransitionStrategy(0.5));

        double smallBtnWidth = 50;
        double sliderWidth = 220;
        double sliderX = centerX - sliderWidth / 2;

        this.musicSlider = new Slider(sliderX, 255, sliderWidth, 10, settings.getMusicVolume());
        this.musicSlider.setTransition(new WipeElementTransitionStrategy(0.5));

        this.sfxSlider = new Slider(sliderX, 405, sliderWidth, 10, settings.getSfxVolume());
        this.sfxSlider.setTransition(new WipeElementTransitionStrategy(0.5));

        this.prevMusicButton = new Button(sliderX, 320, smallBtnWidth, 40, normalImage, hoveredImage, "<");
        this.prevMusicButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.nextMusicButton = new Button(sliderX + sliderWidth
                - smallBtnWidth, 320, smallBtnWidth, 40, normalImage, hoveredImage, ">");
        this.nextMusicButton.setTransition(new WipeElementTransitionStrategy(0.5));

        String currentMusic = SettingsManager.getInstance().getSelectedMusic();
        for (int i = 0; i < musicTracks.length; i++) {
            if (musicTracks[i].equals(currentMusic)) {
                this.currentMusicIndex = i;
                break;
            }
        }
        this.settingsImage = am.getImage("settings");

        this.window.addButton(musicButton);
        this.window.addButton(sfxButton);
        this.window.addButton(backButton);
        this.window.addButton(musicSlider);
        this.window.addButton(sfxSlider);
        this.window.addButton(prevMusicButton);
        this.window.addButton(nextMusicButton);
    }

    @Override
    public void update(double deltaTime) {
        SoundManager.getInstance().updateAllVolumes();
        window.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        SettingsManager settings = SettingsManager.getInstance();
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        if (settingsImage != null) {
            gc.drawImage(settingsImage, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        }

        window.render(gc);

        boolean musicOn = settings.isMusicEnabled();
        boolean sfxOn = settings.isSfxEnabled();
        musicButton.setText("Music: " + (musicOn ? "ON" : "OFF"));
        sfxButton.setText("SFX: " + (sfxOn ? "ON" : "OFF"));

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(settings.getSelectedMusic(), centerX, 345);
    }

    @Override
    public void handleInput(I_InputProvider input) {
        if (input == null) return;

        window.handleInput(input);

        SettingsManager settings = SettingsManager.getInstance();

        if (musicButton.isClicked()) {
            settings.toggleMusic();

            if (settings.isMusicEnabled()) {
                SoundManager.getInstance().playSelectedMusic();
            } else {
                SoundManager.getInstance().stopMusic();
            }
        }

        if (sfxButton.isClicked()) {
            settings.toggleSfx();
        }

        boolean sfxVolumeChanged = false;

        double newMusicVol = musicSlider.getValue();
        double newSfxVol = sfxSlider.getValue();
        boolean volumeChanged = false;

        if (newMusicVol != settings.getMusicVolume()) {
            settings.setMusicVolume(newMusicVol);
            volumeChanged = true;
        }

        if (newSfxVol != settings.getSfxVolume()) {
            settings.setSfxVolume(newSfxVol);
        }

        boolean trackChanged = false;

        if (prevMusicButton.isClicked()) {
            currentMusicIndex = (currentMusicIndex - 1 + musicTracks.length) % musicTracks.length;
            settings.setSelectedMusic(musicTracks[currentMusicIndex]);
            trackChanged = true;
        }
        if (nextMusicButton.isClicked()) {
            currentMusicIndex = (currentMusicIndex + 1) % musicTracks.length;
            settings.setSelectedMusic(musicTracks[currentMusicIndex]);
            trackChanged = true;
        }

        if (trackChanged) {
            SoundManager.getInstance().playSelectedMusic();
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