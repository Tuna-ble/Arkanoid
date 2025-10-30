package org.example.data;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

public class AssetManager {
    private Map<String, Clip> sounds = new HashMap<>();
    private Map<String, Image> images = new HashMap<>();

    private AssetManager() {

    }

    public void loadAssets() {
        try {
            loadSound("brick_hit", "/sounds/brick_hit.wav");
            loadSound("paddle_hit", "/sounds/paddle_hit.wav");
            loadSound("ball_lost", "/sounds/ball_lost.wav");
        } catch (Exception e) {
            System.err.println("Không thể tải file âm thanh: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            // loadImage("ball", "/images/ball.png");
            // loadImage("paddle", "/images/paddle.png");
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

            loadSound("brick_hit", "/sounds/brick_hit.wav");
            loadSound("paddle_hit", "/sounds/paddle_hit.wav");
            loadSound("ball_lost", "/sounds/ball_lost.wav");

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
}
