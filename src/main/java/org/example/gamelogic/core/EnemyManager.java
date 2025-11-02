package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.enemy.Enemy;
import org.example.gamelogic.entities.enemy.Enemy1;
import org.example.gamelogic.entities.enemy.Enemy2;
import org.example.gamelogic.entities.powerups.*;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.factory.EnemyFactory;
import org.example.gamelogic.factory.PowerUpFactory;
import org.example.gamelogic.registry.EnemyRegistry;
import org.example.gamelogic.registry.PowerUpRegistry;
import org.example.gamelogic.strategy.powerup.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class EnemyManager {
    private static class SingletonHolder {
        private static final EnemyManager INSTANCE = new EnemyManager();
    }

    public static EnemyManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final EnemyFactory enemyFactory;
    private List<Enemy> activeEnemies = new ArrayList<>();

    private static final String[] ENEMY_TYPES = {"E1", "E2"};

    private double levelTimer = 0.0;
    private int enemiesSpawned = 0;
    private final int MAX_ENEMIES_PER_LEVEL = 5;
    private int currentLevelNumber;

    public EnemyManager() {
        EnemyRegistry registry = EnemyRegistry.getInstance();
        registerEnemyPrototypes(registry);
        this.enemyFactory = new EnemyFactory(registry);
    }

    private void registerEnemyPrototypes(EnemyRegistry enemyRegistry) {
        final double POWERUP_WIDTH = 40;
        final double POWERUP_HEIGHT = 40;

        enemyRegistry.register("E1", new Enemy1(0.0, 0.0, GameConstants.ENEMY_WIDTH,
                GameConstants.ENEMY_HEIGHT, 25.0, 50.0));

        enemyRegistry.register("E2", new Enemy2(0.0, 0.0, GameConstants.ENEMY_WIDTH,
                GameConstants.ENEMY_HEIGHT, 25.0, 50.0));
    }

    public void spawnEnemy(String type, double x, double y) {
        Enemy newEnemy = enemyFactory.createEnemy(type, x, y);
        activeEnemies.add(newEnemy);
    }

    public void spawnEnemy(String type) {
        Enemy newEnemy = enemyFactory.createEnemy(type, 0, 0);

        double startX = Math.random() * (GameConstants.SCREEN_WIDTH - newEnemy.getWidth());
        double startY = -newEnemy.getHeight();
        newEnemy.setPosition(startX, startY);

        double horizontalSpeed = newEnemy.getDx(); // Lấy tốc độ ngang (ví dụ: 150)

        // Quyết định 50/50
        if (Math.random() < 0.5) {
            // Nếu random < 0.5, cho đi sang trái
            newEnemy.setDx(-horizontalSpeed); // Đặt dx = -150
        }
        this.activeEnemies.add(newEnemy);
    }

    public void loadLevelScript(int levelNumber) {
        this.currentLevelNumber = levelNumber;
        this.levelTimer = 0.0;
        this.enemiesSpawned = 0;

        this.activeEnemies.clear();
    }

    public void update(double deltaTime) {
        levelTimer += deltaTime;

        switch (this.currentLevelNumber) {
            case 2:
                if (levelTimer > 5.0 && enemiesSpawned < 10) {
                    levelTimer = 0.0;
                    enemiesSpawned++;
                    spawnEnemy("E1");
                }
                break;

            case 3:
                if (levelTimer > 3.0 && enemiesSpawned < 20) {
                    levelTimer = 0.0;
                    enemiesSpawned++;
                    spawnEnemy("E2");
                }
                break;
        }
        activeEnemies.removeIf(Enemy::isDestroyed);
        for (Enemy enemy : activeEnemies) {
            enemy.update(deltaTime);
        }
    }

    public void render(GraphicsContext gc) {
        for (Enemy enemy : activeEnemies) {
            enemy.render(gc);
        }
    }

    public void clear() {
        activeEnemies.clear();
    }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }
}
