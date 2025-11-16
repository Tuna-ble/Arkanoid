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
import org.example.gamelogic.graphics.buttons.*;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.HologramTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;

public final class SettingsState implements GameState {
    private GameState previousState;
    private Window window;
    private AbstractButton musicButton;
    private AbstractButton sfxButton;
    private AbstractButton musicToggleButton;
    private AbstractButton sfxToggleButton;
    private AbstractButton backButton;
    private AbstractButton prevMusicButton;
    private AbstractButton nextMusicButton;
    private AbstractButton musicDisplayLabel;
    private AbstractButton banner;
    private Font titleFont;

    private Slider musicSlider;
    private Slider sfxSlider;

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private Image settingsImage = null;

    private final String[] musicTracks = {"music_1", "music_2", "music_3"};
    private int currentMusicIndex = 0;

    public SettingsState(GameState previousState) {
        this.previousState = previousState;

        AssetManager am = AssetManager.getInstance();
        SettingsManager settings = SettingsManager.getInstance();

        titleFont = am.getFont("Anxel", 48);
        String initialMusicName = settings.getSelectedMusic();

        ITransitionStrategy transition = new HologramTransitionStrategy();
        this.window = new Window(previousState, 600, 400, transition);

        double bannerX = window.getX() + GameConstants.UI_BUTTON_PADDING;
        double bannerY = window.getY() + GameConstants.UI_BUTTON_PADDING;
        double bannerWidth = GameConstants.UI_BANNER_WIDTH;
        double bannerHeight = GameConstants.UI_BANNER_HEIGHT;
        Image bannerImage = am.getImage("banner1");
        this.banner = new Button(bannerX, bannerY, bannerWidth, bannerHeight, bannerImage, bannerImage, "SETTINGS");
        this.banner.setTransition(new WipeElementTransitionStrategy(0.5));

        double btnX = bannerX;
        double firstRowY = bannerY + bannerHeight + 20;
        double secondRowY = firstRowY + 80 + GameConstants.UI_BUTTON_PADDING;
        double backButtonX = window.getX() + window.getWidth() - 90 - GameConstants.UI_BUTTON_PADDING;
        double backButtonY = window.getY() + window.getHeight() - 60 - GameConstants.UI_BUTTON_PADDING;
        boolean musicOn = settings.isMusicEnabled();
        boolean sfxOn = settings.isSfxEnabled();

        final Image sfxImage = am.getImage("sfxIcon");
        final Image musicImage = am.getImage("musicIcon");
        final Image backImage = am.getImage("backButton");
        final Image backHoveredImage = am.getImage("backButtonHovered");
        final Image toggleOnImage = am.getImage("toggleOn");
        final Image toggleOffImage = am.getImage("toggleOff");
        final Image nextImage = am.getImage("nextButton");
        final Image prevImage = am.getImage("prevButton");

        this.sfxButton = new Button(btnX, firstRowY, 80, 80, sfxImage, sfxImage, "");
        this.sfxButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.musicButton = new Button(btnX, secondRowY, 80, 80, musicImage, musicImage, "");
        this.musicButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.backButton = new Button(backButtonX, backButtonY, 90, 60, backImage,
                backHoveredImage, "Back");
        this.backButton.setTransition(new WipeElementTransitionStrategy(0.5));

        double nextBtnX = btnX + 80 + GameConstants.UI_BUTTON_PADDING;
        double toggleButtonWidth = 80;
        double toggleButtonHeight = 35;
        double sliderWidth = 300;
        double sliderPadding = toggleButtonHeight + GameConstants.UI_BUTTON_PADDING;
        double musicTextX = nextBtnX + sliderWidth / 2 - (sliderWidth - 60) / 2;

        this.sfxToggleButton = new ToggleButton(nextBtnX, firstRowY + 10, toggleButtonWidth, toggleButtonHeight,
                toggleOnImage, toggleOffImage, sfxOn);
        this.sfxToggleButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.musicToggleButton = new ToggleButton(nextBtnX, secondRowY + 10, toggleButtonWidth, toggleButtonHeight,
                toggleOnImage, toggleOffImage, musicOn);
        this.musicToggleButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.sfxSlider = new Slider(nextBtnX, firstRowY + sliderPadding,
                sliderWidth, 10, settings.getSfxVolume());
        this.sfxSlider.setTransition(new WipeElementTransitionStrategy(0.5));

        this.musicSlider = new Slider(nextBtnX, secondRowY + sliderPadding ,
                sliderWidth, 10, settings.getMusicVolume());
        this.musicSlider.setTransition(new WipeElementTransitionStrategy(0.5));

        this.prevMusicButton = new Button(nextBtnX, secondRowY + sliderPadding + 20,
                30, 30, prevImage, prevImage, "");
        this.prevMusicButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.nextMusicButton = new Button(nextBtnX + sliderWidth - 30, secondRowY + sliderPadding + 20,
                30, 30, nextImage, nextImage, "");
        this.nextMusicButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.musicDisplayLabel = new TextLabel(musicTextX, secondRowY + sliderPadding + 20,
                sliderWidth - 60, 30, initialMusicName);
        this.musicDisplayLabel.setTransition(new WipeElementTransitionStrategy(0.5));

        for (int i = 0; i < musicTracks.length; i++) {
            if (musicTracks[i].equals(initialMusicName)) {
                this.currentMusicIndex = i;
                break;
            }
        }
        this.settingsImage = am.getImage("settings");

        this.window.addButton(banner);
        this.window.addButton(musicButton);
        this.window.addButton(sfxButton);
        this.window.addButton(backButton);
        this.window.addButton(sfxToggleButton);
        this.window.addButton(musicToggleButton);
        this.window.addButton(musicSlider);
        this.window.addButton(sfxSlider);
        this.window.addButton(prevMusicButton);
        this.window.addButton(nextMusicButton);
        this.window.addButton(musicDisplayLabel);
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
    }

    @Override
    public void handleInput(I_InputProvider input) {
        if (input == null) return;

        window.handleInput(input);

        SettingsManager settings = SettingsManager.getInstance();

        if (musicToggleButton.isClicked()) {
            settings.toggleMusic();

            if (settings.isMusicEnabled()) {
                SoundManager.getInstance().playSelectedMusic();
            } else {
                SoundManager.getInstance().stopMusic();
            }
        }

        if (sfxToggleButton.isClicked()) {
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
            String newMusicName = musicTracks[currentMusicIndex];
            SoundManager.getInstance().playSelectedMusic();

            musicDisplayLabel.setText(newMusicName);
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