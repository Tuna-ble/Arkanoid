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

    /**
     * Khởi tạo AssetManager và load toàn bộ tài nguyên mặc định.
     * <br>Input: không có (chỉ dùng nội bộ). Output: cache nội bộ được điền.
     */
    private AssetManager() {
        loadAssets();
    }

    private static class SingletonHolder {
        private static final AssetManager INSTANCE = new AssetManager();
    }

    /**
     * Lấy instance singleton của AssetManager.
     *
     * @return đối tượng AssetManager dùng chung trong toàn bộ game
     */
    public static AssetManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * Load tất cả âm thanh, hình ảnh và font mặc định vào cache.
     * <br>Input: không có. Output: các map sounds/images/fontFamilies được cập nhật.
     */
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
            loadSound("victory", "/sounds/victory.wav");
            loadSound("star_accounted", "/sounds/star_accounted.wav");
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
            loadImage("powerups", "/images/PowerupSprites.png");

            loadImage("enemy1", "/images/UFO.png");
            loadImage("enemy2", "/images/asteroid.png");
            loadImage("boss", "/images/moai.png");
            loadImage("bossShoot", "/images/moaiShooting.png");
            loadImage("bossEnraged", "/images/angryMoai.png");
            loadImage("bossEnragedShoot", "/images/angryMoaiShooting.png");
            loadImage("bossHit", "/images/moaiHit.png");
            loadImage("bossEnragedHit", "/images/angryMoaiHit.png");
            loadImage("minion", "/images/minion.png");
            loadImage("minionShoot", "/images/minionShoot.png");
            loadImage("enemyExplode",  "/images/enemyExplode.png");

            loadImage("frame", "/images/GameFrame.png");
            loadImage("pause", "/images/pause.png");
            loadImage("hud", "/images/hud.png");

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
            loadImage("bullet", "/images/bullet.png");
            loadImage("bossBullet", "/images/bossBullet.png");

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
            loadImage("bossBackground", "/images/bossBackground.png");
            loadImage("infiniteBackground", "/images/infiniteBackground.png");

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

    /**
     * Load một file âm thanh từ resource và lưu vào map {@code sounds}.
     *
     * @param name key dùng để truy cập âm thanh
     * @param path đường dẫn trong classpath tới file âm thanh
     * @throws Exception nếu không tìm thấy resource hoặc lỗi khi tạo Clip
     */
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

    /**
     * Load một file hình ảnh từ resource và lưu vào map {@code images}.
     *
     * @param name key dùng để truy cập ảnh
     * @param path đường dẫn trong classpath tới file ảnh
     * @throws Exception nếu không tìm thấy resource hoặc lỗi khi đọc ảnh
     */
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

    /**
     * Lấy một âm thanh đã được cache.
     *
     * @param name key của âm thanh cần lấy
     * @return {@link Clip} tương ứng, hoặc {@code null} nếu không tồn tại
     */
    public Clip getSound(String name) {
        Clip clip = sounds.get(name);
        if (clip == null) {
            System.err.println("Yêu cầu âm thanh không tồn tại: " + name);
        }
        return clip;
    }

    /**
     * Lấy một hình ảnh đã được cache.
     *
     * @param name key của ảnh cần lấy
     * @return {@link Image} tương ứng, hoặc {@code null} nếu không tồn tại
     */
    public Image getImage(String name) {
        Image img = images.get(name);
        if (img == null) {
            System.err.println("Yêu cầu hình ảnh không tồn tại: " + name);
        }
        return img;
    }

    /**
     * Load một font từ resource và lưu lại family name vào {@code fontFamilies}.
     *
     * @param name key của font
     * @param path đường dẫn resource tới file font
     * @throws Exception nếu không tìm thấy file hoặc lỗi khi load font
     */
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

    /**
     * Cache trực tiếp một đối tượng {@link Font} với key cho trước.
     *
     * @param name key của font
     * @param font đối tượng font cần cache
     */
    public void cacheFont(String name, Font font) {
        fonts.put(name, font);
    }

    /**
     * Tạo và trả về font theo family đã load trước đó.
     *
     * @param name key của font (đã load bằng {@link #loadFont(String, String)})
     * @param size kích thước font mong muốn
     * @return font với family tương ứng; nếu không có sẽ trả về Arial với size cho trước
     */
    public Font getFont(String name, double size) {
        String familyName = fontFamilies.get(name);

        if (familyName != null) {
            return new Font(familyName, size);
        } else {
            System.err.println("Yêu cầu font không tồn tại: " + name + ". Dùng Arial.");
            return new Font("Arial", size);
        }
    }

    /**
     * Cache một {@link Effect} để tái sử dụng.
     *
     * @param name   key cho effect
     * @param effect effect cần cache
     */
    public void cacheEffect(String name, Effect effect) {
        effects.put(name, effect);
    }

    /**
     * Lấy một effect đã được cache.
     *
     * @param name key của effect cần lấy
     * @return {@link Effect} tương ứng, hoặc {@code null} nếu không tồn tại
     */
    public Effect getEffect(String name) {
        Effect effect = effects.get(name);
        if (effect == null) {
            System.err.println("Yêu cầu effect không tồn tại: " + name);
        }
        return effect;
    }

    /**
     * Cache một {@link Paint} (thường là gradient) để tái sử dụng.
     *
     * @param name     key cho gradient
     * @param gradient đối tượng Paint cần cache
     */
    public void cacheGradient(String name, Paint gradient) {
        gradients.put(name, gradient);
    }

    /**
     * Lấy một gradient đã được cache.
     *
     * @param name key của gradient
     * @return {@link Paint} tương ứng, hoặc {@code null} nếu không tồn tại
     */
    public Paint getGradient(String name) {
        Paint gradient = gradients.get(name);
        if (gradient == null) {
            System.err.println("Yêu cầu gradient không tồn tại: " + name);
        }
        return gradient;
    }

    /**
     * Load toàn bộ tài nguyên và cache một số font hay dùng nếu chưa preload.
     * <br>Input: không có. Output: tài nguyên đảm bảo đã sẵn sàng dùng.
     */
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
     * Public wrapper để load thêm một ảnh vào cache trong runtime.
     *
     * @param name key cho ảnh
     * @param path đường dẫn resource tới file ảnh
     * @throws Exception nếu không tìm thấy hoặc lỗi khi load ảnh
     */
    public synchronized void loadImageResource(String name, String path) throws Exception {
        loadImage(name, path);
    }
}