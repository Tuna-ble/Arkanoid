package org.example.gamelogic.core;

import org.example.data.ILevelRepository;
import org.example.data.LevelData;
import org.example.gamelogic.entities.bricks.*;
import org.example.gamelogic.factory.BrickFactory;
import org.example.gamelogic.registry.BrickRegistry;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public final class BrickManager {
    private final ILevelRepository levelRepository;
    private final BrickFactory brickFactory;
    private List<Brick> bricks;

    public BrickManager(ILevelRepository levelRepository) {
        this.bricks = new ArrayList<>();
        this.levelRepository = levelRepository;
        BrickRegistry registry = BrickRegistry.getInstance();
        registerBrickPrototypes(registry);
        this.brickFactory = new BrickFactory(registry);
    }

    private void registerBrickPrototypes(BrickRegistry brickRegistry) {
        final double TILE_WIDTH = 60;
        final double TILE_HEIGHT = 20;

        brickRegistry.register("N", new NormalBrick(0,  0, TILE_WIDTH, TILE_HEIGHT));
        brickRegistry.register("H", new HardBrick(0,  0, TILE_WIDTH, TILE_HEIGHT));
        brickRegistry.register("E", new ExplosiveBrick(0,  0, TILE_WIDTH, TILE_HEIGHT));
    }

    public void update(double deltaTime) {
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
        for (int row = 0; row < layout.size(); row++) {
            String[] types = layout.get(row).split(" ");
            for (int col = 0; col < types.length; col++) {
                String type = types[col];
                if (type.equals("_")) {
                    continue;
                }
                double x = col * 65.0;
                double y = row * 25.0 + 50;
                Brick brick = brickFactory.createBrick(type, x, y);
                if (brick != null) {
                    this.bricks.add(brick);
                }
            }
        }
    }

    public boolean isLevelComplete() {
        return bricks.isEmpty();
    }
}
