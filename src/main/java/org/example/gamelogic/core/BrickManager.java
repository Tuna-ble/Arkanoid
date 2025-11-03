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

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        Iterator<Brick> iterator=bricks.iterator();
        while(iterator.hasNext()) {
            Brick other=iterator.next();
            if (event.getExplosiveBrick().withinRangeOf(other)) {
                EventManager.getInstance().
                        publish(new BrickDamagedEvent(other, event.getExplosiveBrick().getGameObject()));
            }
        }
    }

    private void registerBrickPrototypes(BrickRegistry brickRegistry) {
        final double TILE_WIDTH = 60;
        final double TILE_HEIGHT = 20;

        brickRegistry.register("H", new HardBrick(0,  0, TILE_WIDTH, TILE_HEIGHT));
        brickRegistry.register("N", new NormalBrick(0,  0, TILE_WIDTH, TILE_HEIGHT));
        brickRegistry.register("U", new UnbreakableBrick(0, 0, TILE_WIDTH, TILE_HEIGHT));
        brickRegistry.register("E", new ExplosiveBrick(0,  0, TILE_WIDTH, TILE_HEIGHT));
        brickRegistry.register("R", new HealingBrick(0, 0, TILE_WIDTH, TILE_HEIGHT));
        /// HNUE :)))
        /// bựa
    }

    public void update(double deltaTime) {
        System.out.println("there are "+bricks.size()+" exits");
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

    public void loadLevel(int levelNumber) {
        this.bricks.clear();
        LevelData levelData = levelRepository.loadLevel(levelNumber);

        List<String> layout = levelData.getLayout();

        int maxCols = 0;
        for (String row : layout) {
            int numCols = row.trim().split("\\s+").length;
            if (numCols > maxCols) {
                maxCols = numCols;
            }
        }

        double gridWidth = maxCols * (GameConstants.BRICK_WIDTH + GameConstants.PADDING) - GameConstants.PADDING;

        double startX = (GameConstants.SCREEN_WIDTH - gridWidth) / 2.0;

        for (int row = 0; row < layout.size(); row++) {
            String[] types = layout.get(row).split(" ");
            for (int col = 0; col < types.length; col++) {
                String type = types[col];
                if (type.equals("_")) {
                    continue;
                }
                double x = startX + col * (GameConstants.BRICK_WIDTH + GameConstants.PADDING);
                double y = GameConstants.TOP_MARGIN + row * (GameConstants.BRICK_HEIGHT + GameConstants.PADDING);
                Brick brick = brickFactory.createBrick(type, x, y);
                if (brick != null) {
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
}
