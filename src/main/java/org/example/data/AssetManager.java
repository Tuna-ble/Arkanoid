package org.example.data;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;

public class AssetManager {
    private Map<String, Clip> sounds = new HashMap<>();
    private Map<String, Image> images = new HashMap<>();
    private Map<String, Font> fonts = new HashMap<>();
    private Map<String, Effect> effects = new HashMap<>();
    private Map<String, Paint> gradients = new HashMap<>();
    private Map<String, String> fontFamilies = new HashMap<>();

    private boolean resourcesPreloaded = false;

    private AssetManager() {
        loadAssets();
    }

    private static class SingletonHolder {
        private static final AssetManager INSTANCE = new AssetManager();
    }

    public static AssetManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void loadAssets() {
        try {
            loadSound("brick_destroyed", "/sounds/brick_destroyed.wav");
            loadSound("paddle_hit", "/sounds/paddle_hit.wav");
            loadSound("ball_lost", "/sounds/ball_lost.wav");
            loadSound("glass", "/sounds/glass.wav");
            loadSound("powerup", "/sounds/powerup.wav");
            loadSound("brick_hit", "/sounds/brick_hit.wav");
            loadSound("bomb", "/sounds/Bomb.wav");
            loadSound("music_1", "/sounds/music_1.wav");
            loadSound("music_2", "/sounds/music_2.wav");
            loadSound("music_3", "/sounds/music_3.wav");
            loadSound("siren", "/sounds/siren.wav");
        } catch (Exception e) {
            System.err.println("Không thể tải file âm thanh: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            // loadImage("ball", "/images/ball.png");
            loadImage("paddle", "/images/paddle.png");
            loadImage("normalBrick", "/images/cyanBrick.png");
            loadImage("hardBrick1", "/images/hardBrick1.png");
            loadImage("hardBrick2", "/images/hardBrick2.png");
            loadImage("hardBrick3", "/images/hardBrick3.png");
            loadImage("healingBrick", "/images/greenBrick.png");
            loadImage("explosiveBrick", "/images/redBrick.png");
            loadImage("unbreakableBrick", "/images/unbreakableBrick.png");
            loadImage("unbreakableBrickHit", "/images/unbreakableBrickHit.png");

            loadImage("enemy1", "/images/UFO.png");
            loadImage("boss", "/images/moai.png");
            loadImage("bossShoot", "/images/moaiShooting.png");
            loadImage("minion", "/images/minion.png");
            loadImage("minionShoot", "/images/minionShoot.png");

            loadImage("frame", "/images/GameFrame.png");
            loadImage("pause", "/images/pause.png");

            loadImage("mainMenu", "/images/mainMenu.png"); //
            loadImage("ranking", "/images/ranking.png"); //
            loadImage("settings", "/images/settings.png");
            loadImage("gameMode", "/images/gameMode.png");
            loadImage("modesFrame", "/images/modesFrame.png");//
            loadImage("gameOver", "/images/gameOver.png"); //
            loadImage("victory", "/images/victory.png"); //
            loadImage("level", "/images/level.png"); //
            loadImage("button", "/images/button.png");
            loadImage("hoveredButton", "/images/hoveredButton.png");

            loadImage("page1", "/images/page1.png");
            loadImage("page2", "/images/page2.png");

            loadImage("icon_expand", "/images/expandpaddle.png"); //
            loadImage("icon_extra_life", "/images/extra.png"); //

            loadImage("banner1", "/images/banner1.png");
            loadImage("banner2", "/images/banner2.png");
            loadImage("sfxIcon", "/images/sfxIcon.png");
            loadImage("musicIcon", "/images/musicIcon.png");
            loadImage("backButton", "/images/backButton.png");
            loadImage("backButtonHovered", "/images/backButtonHovered.png");
            loadImage("nextButton", "/images/nextButton.png");
            loadImage("prevButton", "/images/prevButton.png");
            loadImage("hologram", "/images/hologram.png");
            loadImage("popup_fill", "/images/hologram.png");
            loadImage("textButton", "/images/textButton.png");
            loadImage("selectButton", "/images/selectButton.png");
            loadImage("selectButtonHovered", "/images/selectButtonHovered.png");

            loadImage("toggleOn", "/images/toggleOn.png");
            loadImage("toggleOff", "/images/toggleOff.png");
            loadImage("barFrame", "/images/barFrame.png");
            loadImage("barFill", "/images/barFill.png");
            loadImage("barHandle", "/images/barHandle.png");
            loadImage("rankBanner", "/images/rankBanner.png");

            loadImage("infiniteStatic", "/images/infiniteStatic.png");
            loadImage("infinite", "/images/infinite.png");

            loadImage("bg1", "/images/background1.png");
            loadImage("bg2", "/images/background2.png");
            loadImage("bg3", "/images/background3.png");

        } catch (Exception e) {
            System.err.println("Không thể tải file hình ảnh: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            loadFont("Anxel", "/fonts/Anxel.ttf");

        } catch (Exception e) {
            System.err.println("Lỗi nghiêm trọng khi tải fonts: " + e.getMessage());
        }
    }

    private void loadSound(String name, String path) throws Exception {
        InputStream audioSrc = getClass().getResourceAsStream(path);
        if (audioSrc == null) {
            throw new Exception("Không tìm thấy file tài nguyên: " + path);
        }

        try (InputStream bufferedIn = new BufferedInputStream(audioSrc);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn)) {

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            sounds.put(name, clip);
        }
    }
    private void loadImage(String name, String path) throws Exception {
        InputStream imageStream = getClass().getResourceAsStream(path);
        if (imageStream == null) {
            throw new Exception("Không tìm thấy file tài nguyên: " + path);
        }

        try (imageStream) {
            Image image = new Image(imageStream);
            images.put(name, image);
        }
    }

    public Clip getSound(String name) {
        Clip clip = sounds.get(name);
        if (clip == null) {
            System.err.println("Yêu cầu âm thanh không tồn tại: " + name);
        }
        return clip;
    }

    public Image getImage(String name) {
        Image img = images.get(name);
        if (img == null) {
            System.err.println("Yêu cầu hình ảnh không tồn tại: " + name);
        }
        return img;
    }

    private void loadFont(String name, String path) throws Exception {
        try (InputStream fontStream = getClass().getResourceAsStream(path)) {
            if (fontStream == null) {
                throw new Exception("Không tìm thấy file font: " + path);
            }

            Font loadedFont = Font.loadFont(fontStream, 1);

            if (loadedFont == null) {
                throw new Exception("Lỗi khi tải font: " + path);
            }
            String fontFamilyName = loadedFont.getFamily();

            fontFamilies.put(name, fontFamilyName);
        }
    }

    public void cacheFont(String name, Font font) {
        fonts.put(name, font);
    }

    public Font getFont(String name, double size) {
        String familyName = fontFamilies.get(name);

        if (familyName != null) {
            return new Font(familyName, size);
        } else {
            System.err.println("Yêu cầu font không tồn tại: " + name + ". Dùng Arial.");
            return new Font("Arial", size);
        }
    }

    public void cacheEffect(String name, Effect effect) {
        effects.put(name, effect);
    }

    public Effect getEffect(String name) {
        Effect effect = effects.get(name);
        if (effect == null) {
            System.err.println("Yêu cầu effect không tồn tại: " + name);
        }
        return effect;
    }

    public void cacheGradient(String name, Paint gradient) {
        gradients.put(name, gradient);
    }

    public Paint getGradient(String name) {
        Paint gradient = gradients.get(name);
        if (gradient == null) {
            System.err.println("Yêu cầu gradient không tồn tại: " + name);
        }
        return gradient;
    }

    public void preloadResources() {
        if (resourcesPreloaded) return;

        loadAssets();
        
        // Cache common fonts
        cacheFont("title", new Font("Arial", 70));
        cacheFont("score", new Font("Arial", 40));
        cacheFont("rank", new Font("Arial", 32));
        
        resourcesPreloaded = true;
    }

    /**
     * Public wrapper to load an image into the internal cache. Safe to call
     * from initialization code. If loading fails it will throw an exception
     * to the caller so they can decide how to handle it.
     */
    public synchronized void loadImageResource(String name, String path) throws Exception {
        loadImage(name, path);
    }
}