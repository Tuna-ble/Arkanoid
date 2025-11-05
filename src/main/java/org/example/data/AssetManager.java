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
    
    private boolean resourcesPreloaded = false;

    private AssetManager() {
        loadAssets();  // Load assets ngay khi khởi tạo
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
        } catch (Exception e) {
            System.err.println("Không thể tải file âm thanh: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            // loadImage("ball", "/images/ball.png");
            // loadImage("paddle", "/images/paddle.png");
            loadImage("frame", "/GameIcon/Frame.png");
        } catch (Exception e) {
            System.err.println("Không thể tải file hình ảnh: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class SingletonHolder {
        private static final AssetManager INSTANCE = new AssetManager();
    }

    public static AssetManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void loadSounds() {
        try {

            loadSound("brick_destroyed", "/sounds/brick_destroyed.wav");
            loadSound("paddle_hit", "/sounds/paddle_hit.wav");
            loadSound("ball_lost", "/sounds/ball_lost.wav");
            loadSound("powerup", "/sounds/powerup.wav");
            loadSound("brick_hit", "/sounds/brick_hit.wav");
            loadSound("bomb", "/sounds/bomb.wav");

        } catch (Exception e) {
            System.err.println("Không thể tải file âm thanh: " + e.getMessage());
            e.printStackTrace();
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

    public void cacheFont(String name, Font font) {
        fonts.put(name, font);
    }

    public Font getFont(String name) {
        Font font = fonts.get(name);
        if (font == null) {
            System.err.println("Yêu cầu font không tồn tại: " + name);
        }
        return font;
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