package org.example.gamelogic.core;

import org.example.data.ILevelRepository;
import org.example.data.LevelData;
import org.example.gamelogic.entities.bricks.*;
import org.example.gamelogic.events.BrickDamagedEvent;
import org.example.gamelogic.events.ExplosiveBrickEvent;
import org.example.gamelogic.events.BallHitBrickEvent;
import org.example.gamelogic.factory.BrickFactory;
import org.example.gamelogic.registry.BrickRegistry;
import org.example.config.GameConstants;
import org.example.data.SavedGameState;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public final class BrickManager {
    private ILevelRepository levelRepository;
    private final BrickFactory brickFactory;
    private List<Brick> bricks;

    public BrickManager(ILevelRepository levelRepository) {
        this.bricks = new ArrayList<>();
        this.levelRepository = levelRepository;
        BrickRegistry registry = BrickRegistry.getInstance();
        registerBrickPrototypes(registry);
        this.brickFactory = new BrickFactory(registry);

        subscribeToExplosiveBrickEvent();
    }

    private void subscribeToExplosiveBrickEvent() {
        EventManager.getInstance().subscribe(
                ExplosiveBrickEvent.class,
                this::onBrickExploded
        );
    }

    private void onBrickExploded(ExplosiveBrickEvent event) {
        Iterator<Brick> iterator = bricks.iterator();
        while(iterator.hasNext()) {
            Brick other=iterator.next();
            if (event.getExplosiveBrick().withinRangeOf(other)) {
                EventManager.getInstance().
                        publish(new BrickDamagedEvent(other, event.getExplosiveBrick().getGameObject()));
            }
        }
    }

    private void registerBrickPrototypes(BrickRegistry brickRegistry) {
        brickRegistry.register("H", new HardBrick(0,  0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        brickRegistry.register("N", new NormalBrick(0,  0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        brickRegistry.register("U", new UnbreakableBrick(0, 0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        brickRegistry.register("E", new ExplosiveBrick(0,  0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        brickRegistry.register("R", new HealingBrick(0, 0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        /// HNUE :)))
        /// bựa
    }

    public void update(double deltaTime) {
        for (Brick brick : bricks) {
            brick.update(deltaTime);
        }
        bricks.removeIf(Brick::isDestroyed);
    }

    public void render(GraphicsContext gc) {
        for (Brick brick : bricks) {
            brick.render(gc);
        }
    }

    public void render(GraphicsContext gc, double timer, double duration) {
        if (bricks.isEmpty()) {
            return;
        }

        double timePerBrick = duration / bricks.size();

        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            double brickStartTime = i * timePerBrick;

            if (timer < brickStartTime) {
                break;
            }

            double timeSinceSpawn = timer - brickStartTime;
            double alpha = Math.min(1.0, timeSinceSpawn / timePerBrick);

            gc.save();
            gc.setGlobalAlpha(alpha);
            brick.render(gc);
            gc.restore();
        }
    }

    public void loadLevel(int levelNumber) {
        this.bricks.clear();
        LevelData levelData = levelRepository.loadLevel(levelNumber);

        List<String> layout = levelData.getLayout();

        for (int row = 0; row < layout.size(); row++) {
            String[] types = layout.get(row).trim().split("\\s+");
            int totalCols = types.length;
            if (totalCols == 0) continue;

            double brickWidth = GameConstants.BRICK_WIDTH; //
            double padding = GameConstants.PADDING; //

            double rowWidth = totalCols * (brickWidth + padding) - padding;
            double rowStartX = (GameConstants.PLAY_AREA_WIDTH - rowWidth) / 2.0;

            for (int col = 0; col < totalCols; col++) {
                String type = types[col];
                if (type.equals("_")) {
                    continue;
                }
                double x = GameConstants.PLAY_AREA_X + rowStartX + col * (brickWidth + padding);
                double y = GameConstants.PLAY_AREA_Y + GameConstants.TOP_MARGIN + row * (GameConstants.BRICK_HEIGHT + padding); //

                Brick brick = brickFactory.createBrick(type, x, y);
                if (brick != null) {

                    int brickId = (row * 100) + currentCol;
                    brick.setId(brickId);
                    this.bricks.add(brick);
                }
            }
        }
    }

    public boolean isLevelComplete() {
        for (Brick brick : bricks) {
            if (brick.isBreakable()) {
                return false;
            }
        }
        return true;
    }

    public void setLevelRepository(ILevelRepository repo) {
        this.levelRepository = repo;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public List<SavedGameState.BrickData> getDataToSave() {
        List<SavedGameState.BrickData> brickDataList = new ArrayList<>();
        for (Brick brick : bricks) {
            brickDataList.add(new SavedGameState.BrickData(
                    brick.getId(),
                    brick.getHealth(),
                    brick.isDestroyed()
            ));
        }
        return brickDataList;
    }

    public void loadData(List<SavedGameState.BrickData> savedBrickDataList) {

        Set<Integer> savedBrickIds = new HashSet<>();
        Map<Integer, Integer> savedBrickHealth = new HashMap<>();

        for (SavedGameState.BrickData data : savedBrickDataList) {
            savedBrickIds.add(data.id);
            savedBrickHealth.put(data.id, data.health);
        }

        System.out.println("--- DEBUG (LOGIC MỚI): Bắt đầu tải gạch ---");
        System.out.println("Số gạch CÒN SỐNG đã lưu: " + savedBrickIds.size());
        System.out.println("Tổng số gạch MỚI (từ loadLevel): " + this.bricks.size());

        Iterator<Brick> iterator = this.bricks.iterator();
        int destroyedCount = 0;

        while (iterator.hasNext()) {
            Brick newBrick = iterator.next();
            int newBrickId = newBrick.getId();

            if (savedBrickIds.contains(newBrickId)) {

                int savedHealth = savedBrickHealth.get(newBrickId);
                newBrick.setHealth(savedHealth);
            } else {

                newBrick.setDestroyed(true);
                destroyedCount++;
            }
        }

        System.out.println("Số gạch đã bị phá hủy (được tải): " + destroyedCount);
        System.out.println("--- DEBUG (LOGIC MỚI): Kết thúc tải gạch ---");

        bricks.removeIf(Brick::isDestroyed);
    }
}