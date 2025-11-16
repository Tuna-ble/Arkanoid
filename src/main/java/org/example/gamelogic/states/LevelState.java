package org.example.gamelogic.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.*;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.core.ProgressManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.graphics.windows.Window;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ScrollDownTransitionStrategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LevelState implements GameState {

    private static final int NUM_LEVELS = 5;
    private final Image levelBackground;
    private double elapsedTime = 0;

    private final AbstractButton banner;
    private final AbstractButton section1Button;
    private final AbstractButton section2Button;
    private final AbstractButton prevLevelButton;
    private final AbstractButton nextLevelButton;
    private final AbstractButton playButton;
    private final AbstractButton backButton;

    private int currentLevelIndex = 0;
    private final LevelData[] allLevelData;
    private final Set<String> currentBrickTypes = new HashSet<>();
    private final Map<String, Image> brickIconMap = new HashMap<>();

    private final Font titleFont;
    private final Font levelNameFont;
    private final Font infoFont;

    private final Window window;

    private final int maxLevelUnlocked;
    private final Map<Integer, Integer> starsMap;

    public LevelState() {
        this.maxLevelUnlocked = ProgressManager.getMaxLevelUnlocked();
        this.starsMap = ProgressManager.loadStars();

        ITransitionStrategy transition = new ScrollDownTransitionStrategy();
        this.window = new Window(null, 850, 650, transition);

        AssetManager am = AssetManager.getInstance();
        titleFont = am.getFont("Anxel", 70);
        levelNameFont = am.getFont("Anxel", 32);
        infoFont = am.getFont("Anxel", 18);

        this.levelBackground = am.getImage("level");
        Image bannerImage = am.getImage("banner2");
        Image page1Image = am.getImage("page1");
        Image page2Image = am.getImage("page2");
        Image normalImage = am.getImage("selectButton");
        Image hoverImage = am.getImage("selectButtonHovered");
        Image nextImage = am.getImage("nextButton");
        Image prevImage = am.getImage("prevButton");

        brickIconMap.put("N", am.getImage("normalBrick"));
        brickIconMap.put("H", am.getImage("hardBrick1"));
        brickIconMap.put("E", am.getImage("explosiveBrick"));
        brickIconMap.put("U", am.getImage("unbreakableBrick"));
        brickIconMap.put("R", am.getImage("healingBrick"));

        this.allLevelData = new LevelData[NUM_LEVELS];
        ILevelRepository repo = new FileLevelRepository();
        for (int i = 0; i < NUM_LEVELS; i++) {
            this.allLevelData[i] = repo.loadLevel(i + 1);
        }

        double previewX = 80, previewY = 180, previewWidth = 490, previewHeight = 350;
        double infoX = 600, infoY = 180, infoWidth = 250, infoHeight = 350;
        double bannerX = window.getX() + window.getWidth() / 2.0 - GameConstants.UI_BANNER_WIDTH / 2.0;
        double bannerY = window.getY() + GameConstants.UI_BUTTON_PADDING + 10;
        this.banner = new Button(bannerX, bannerY, GameConstants.UI_BANNER_WIDTH, GameConstants.UI_BANNER_HEIGHT,
                bannerImage, bannerImage, "LEVELS");
        this.banner.setTransition(new WipeElementTransitionStrategy(0.5));

        this.section1Button = new Button(previewX, previewY, previewWidth, previewHeight,
                page1Image, page1Image, "");
        this.section1Button.setTransition(new WipeElementTransitionStrategy(0.5));

        this.section2Button = new Button(infoX, infoY, infoWidth, infoHeight,
                page2Image, page2Image, "");
        this.section1Button.setTransition(new WipeElementTransitionStrategy(0.5));

        this.prevLevelButton = new Button(previewX - 25, previewY + (previewHeight / 2) - 30,
                35, 60, prevImage, prevImage, "");
        this.prevLevelButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.nextLevelButton = new Button(previewX + previewWidth - 10, previewY + (previewHeight / 2) - 30,
                35, 60, nextImage, nextImage, "");
        this.nextLevelButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.playButton = new Button(previewX + (previewWidth - 200) / 2, previewY + previewHeight + 60,
                200, 60, normalImage, hoverImage, "PLAY");
        this.playButton.setTransition(new WipeElementTransitionStrategy(0.5));

        this.backButton = new Button(infoX + (infoWidth - 200) / 2, infoY + infoHeight + 60,
                200, 60, normalImage, hoverImage, "Back");
        this.backButton.setTransition(new WipeElementTransitionStrategy(0.5));

        updateSelectedLevelInfo();
        window.addButton(banner);
        window.addButton(section1Button);
        window.addButton(section2Button);
        window.addButton(prevLevelButton);
        window.addButton(nextLevelButton);
        window.addButton(playButton);
        window.addButton(backButton);
    }

    private void updateSelectedLevelInfo() {
        currentBrickTypes.clear();
        if (currentLevelIndex < 0 || currentLevelIndex >= allLevelData.length) return;

        LevelData data = allLevelData[currentLevelIndex];
        if (data == null) return;

        for (String row : data.getLayout()) {
            String[] types = row.trim().split("\\s+");
            for (String type : types) {
                if (!type.equals("_") && brickIconMap.containsKey(type)) {
                    currentBrickTypes.add(type);
                }
            }
        }

        int currentLevel = currentLevelIndex + 1;
        if (currentLevel > maxLevelUnlocked) {
            playButton.setDisabled(true);
        } else {
            playButton.setDisabled(false);
        }
    }

    private String getBrickName(String type) {
        switch (type.toUpperCase()) {
            case "N": return "Normal";
            case "H": return "Hard";
            case "E": return "Explosive";
            case "U": return "Unbreakable";
            case "R": return "Healing";
            default: return "Unknown";
        }
    }

    private void renderMiniMap(GraphicsContext gc, LevelData data, double x, double y, double w, double h) {
        if (data == null) return;
        List<String> layout = data.getLayout();
        if (layout.isEmpty()) return;

        int numRows = layout.size();
        int numCols = layout.get(0).trim().split("\\s+").length;
        if (numCols == 0) return;

        double miniBrickWidth = w / numCols;
        double miniBrickHeight = h / numRows;

        for (int row = 0; row < numRows; row++) {
            String[] types = layout.get(row).trim().split("\\s+");
            for (int col = 0; col < types.length; col++) {
                Image brickImage = this.brickIconMap.get(types[col].toUpperCase());

                if (brickImage != null) {
                    double drawX = x + (col * miniBrickWidth);
                    double drawY = y + (row * miniBrickHeight);

                    gc.drawImage(brickImage, drawX, drawY, miniBrickWidth, miniBrickHeight);
                }
            }
        }
    }

    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
        window.update(deltaTime);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(levelBackground, 0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        window.render(gc);

        if (window.transitionFinished()) {
            double previewX = 100, previewY = 180, previewWidth = 450, previewHeight = 350;

            int currentLevel = currentLevelIndex + 1;
            boolean isLocked = (currentLevel > maxLevelUnlocked);

            renderMiniMap(gc, allLevelData[currentLevelIndex],
                    previewX + 10, previewY + 10,
                    previewWidth - 20, previewHeight - 20);

            if (isLocked) {
                gc.setFill(new Color(0, 0, 0, 0.7)); // Màu đen mờ 70%
                gc.fillRect(previewX, previewY, previewWidth, previewHeight);

                gc.setTextAlign(TextAlignment.CENTER);
                TextRenderer.drawOutlinedText(gc, "LOCKED",
                        previewX + previewWidth / 2,
                        previewY + previewHeight / 2 + (levelNameFont.getSize() / 2),
                        levelNameFont, Color.RED, Color.BLACK, 2.0, null);
            }

            gc.setTextAlign(TextAlignment.CENTER);

            String levelName = "Level " + (currentLevelIndex + 1);
            TextRenderer.drawOutlinedText(gc, levelName, previewX + previewWidth / 2, previewY + previewHeight + 40, levelNameFont, Color.WHITE, Color.BLACK, 2.0, null);

            double infoX = 600, infoY = 180, infoWidth = 250, infoHeight = 350;

            TextRenderer.drawOutlinedText(gc, "INFO", infoX + infoWidth / 2, infoY + 50, levelNameFont, Color.WHITE, Color.BLACK, 2.0, null);

            double yOffset = 80;
            double iconWidth = 30;
            double iconHeight = 20;
            gc.setTextAlign(TextAlignment.LEFT);
            for (String type : currentBrickTypes) {
                Image icon = brickIconMap.get(type);
                String name = getBrickName(type);

                gc.drawImage(icon, infoX + 20, infoY + yOffset, iconWidth, iconHeight);
                TextRenderer.drawOutlinedText(gc, ": " + name, infoX + 20 + iconWidth + 5, infoY + yOffset + 15, infoFont, Color.WHITE, Color.BLACK, 1.0, null);
                yOffset += (iconHeight + 15);
            }

            double yOffset2 = infoY + 100;
            gc.setTextAlign(TextAlignment.CENTER);
            TextRenderer.drawOutlinedText(gc, "STARS", infoX + infoWidth / 2, infoY + yOffset2, levelNameFont, Color.WHITE, Color.BLACK, 2.0, null);

            yOffset2 += 40;

            String starText;
            if (isLocked) {
                starText = "---";
            } else {
                int stars = starsMap.getOrDefault(currentLevel, 0);
                starText = getStarString(stars);
            }

            TextRenderer.drawOutlinedText(gc, starText, infoX + infoWidth / 2,
                    infoY + yOffset2, levelNameFont, Color.WHITE, Color.BLACK, 1.0, null);
        }
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) return;
        window.handleInput(inputProvider);

        if (prevLevelButton.isClicked()) {
            currentLevelIndex = (currentLevelIndex - 1 + NUM_LEVELS) % NUM_LEVELS;
            updateSelectedLevelInfo();
        }

        if (nextLevelButton.isClicked()) {
            currentLevelIndex = (currentLevelIndex + 1) % NUM_LEVELS;
            updateSelectedLevelInfo();
        }

        if (playButton.isClicked()) {
            int selectedLevel = currentLevelIndex + 1;

            if (selectedLevel <= maxLevelUnlocked) {
                SaveGameRepository repo = new SaveGameRepository();

                if (repo.hasSave(selectedLevel)) {
                    GameManager gm = GameManager.getInstance();
                    gm.getStateManager().setState(new ConfirmContinueState(selectedLevel));
                } else {
                    EventManager.getInstance().publish(
                            new ChangeStateEvent(GameStateEnum.PLAYING, selectedLevel)
                    );
                }
            }
        }

        if (backButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.GAME_MODE)
            );
        }
    }

    private String getStarString(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i < stars) {
                sb.append("★"); // Sao vàng (hoặc ký tự bạn muốn)
            } else {
                sb.append("☆"); // Sao rỗng
            }
        }
        return sb.toString();
    }
}