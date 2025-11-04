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
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.Button;
import org.example.gamelogic.graphics.TextRenderer;

public final class SettingsState implements GameState {
    private GameState previousState;
    private Button musicButton;
    private Button sfxButton;
    private Button backButton;
    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;
    private final Image settings;

    public SettingsState(GameState previousState) {
        this.previousState = previousState;
        double btnX = centerX - GameConstants.UI_BUTTON_WIDTH / 2;
        boolean musicOn = SettingsManager.getInstance().isMusicEnabled();
        boolean sfxOn = SettingsManager.getInstance().isSfxEnabled();

        this.musicButton = new Button(btnX, 200, "Music: " + (musicOn ? "ON" : "OFF"));
        this.sfxButton = new Button(btnX, 300, "SFX: " + (sfxOn ? "ON" : "OFF"));
        this.backButton = new Button(btnX, 400, "Back");

        this.settings = new Image(getClass().getResourceAsStream("/GameIcon/settings.png"));
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        gc.drawImage(settings, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);
        TextRenderer.drawOutlinedText(
                gc, "SETTINGS", centerX, 110,
                new Font("Arial", 70), Color.WHITE, Color.BLACK, 2.0, null
        );

        boolean musicOn = SettingsManager.getInstance().isMusicEnabled();
        boolean sfxOn = SettingsManager.getInstance().isSfxEnabled();
        musicButton.setText("Music: " + (musicOn ? "ON" : "OFF"));
        sfxButton.setText("SFX: " + (sfxOn ? "ON" : "OFF"));

        musicButton.render(gc);
        sfxButton.render(gc);
        backButton.render(gc);
    }

    @Override
    public void handleInput(I_InputProvider input) {
        if (input == null) return;

        musicButton.update(input);
        sfxButton.update(input);
        backButton.update(input);

        if (musicButton.isClicked()) {
            SettingsManager.getInstance().toggleMusic();
            // (Bạn có thể thêm logic Dừng/Phát nhạc nền ngay tại đây nếu muốn)
        }

        if (sfxButton.isClicked()) {
            SettingsManager.getInstance().toggleSfx();
        }

        if (backButton.isClicked()) {
            // LƯU LẠI SETTINGS KHI THOÁT
            SettingsManager.getInstance().saveSettings();

            // Quay về Main Menu
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.RESUME_GAME)
            );
        }
    }

    public GameState getPreviousState() {
        return this.previousState;
    }
}
