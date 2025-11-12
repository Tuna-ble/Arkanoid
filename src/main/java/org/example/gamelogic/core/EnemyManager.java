package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.enemy.*;
import org.example.gamelogic.factory.EnemyFactory;
import org.example.gamelogic.registry.EnemyRegistry;
import org.example.gamelogic.strategy.bossbehavior.BossDyingStrategy;
import org.example.data.SavedGameState;

import java.util.ArrayList;
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
    private List<Enemy> enemiesToSpawn = new ArrayList<>();

    private GameManager gameManager;
    private BrickManager brickManager;

    private static final String[] ENEMY_TYPES = {"E1", "E2"};

    private double levelTimer = 0.0;
    private int enemiesSpawned = 0;
    private final int MAX_ENEMIES_PER_LEVEL = 5;
    private int currentLevelNumber;
    private boolean bossSpawned;

    public EnemyManager() {
        EnemyRegistry registry = EnemyRegistry.getInstance();
        registerEnemyPrototypes(registry);
        this.enemyFactory = new EnemyFactory(registry);
        this.bossSpawned = false;
    }

    private void registerEnemyPrototypes(EnemyRegistry enemyRegistry) {
        final double POWERUP_WIDTH = 40;
        final double POWERUP_HEIGHT = 40;

        enemyRegistry.register("E1", new Enemy1(0.0, 0.0, GameConstants.ENEMY_WIDTH,
                GameConstants.ENEMY_HEIGHT, 25.0, 50.0));

        enemyRegistry.register("E2", new Enemy2(0.0, 0.0, GameConstants.ENEMY_WIDTH,
                GameConstants.ENEMY_HEIGHT, 25.0, 50.0));
        enemyRegistry.register("MINION", new BossMinion(0.0, 0.0, GameConstants.MINION_WIDTH,
                GameConstants.MINION_HEIGHT, 25.0, 50.0));
        enemyRegistry.register("BOSS", new Boss(0, 0, 0, 100));
    }

    public void spawnEnemy(String type, double centerX, double centerY) {
        Enemy newEnemy = enemyFactory.createEnemy(type, 0, 0);

        double spawnX = centerX - (newEnemy.getWidth() / 2.0);
        double spawnY = centerY - (newEnemy.getHeight() / 2.0);

        newEnemy.setPosition(spawnX, spawnY);
        newEnemy.setHasEnteredScreen(true);

        double horizontalSpeed = newEnemy.getDx();

        if (Math.random() < 0.5) {
            newEnemy.setDx(-horizontalSpeed);
        }

        this.enemiesToSpawn.add(newEnemy);

        if (type.equals("BOSS")) {
            this.bossSpawned = true;
        }
    }

    public void spawnEnemy(String type) {
        Enemy newEnemy = enemyFactory.createEnemy(type, 0, 0);

        double startX = Math.random() * (GameConstants.SCREEN_WIDTH - newEnemy.getWidth());
        double startY = -newEnemy.getHeight();
        newEnemy.setPosition(startX, startY);

        double horizontalSpeed = newEnemy.getDx();

        if (Math.random() < 0.5) {
            newEnemy.setDx(-horizontalSpeed);
        }
        this.enemiesToSpawn.add(newEnemy);

        if (type.equals("BOSS")) {
            this.bossSpawned = true;
        }
    }

    public void loadLevelScript(int levelNumber) {
        this.currentLevelNumber = levelNumber;
        this.levelTimer = 0.0;
        this.enemiesSpawned = 0;

        this.bossSpawned = false;
        this.activeEnemies.clear();
    }

    public void setBrickManager(BrickManager brickManager) {
        this.brickManager = brickManager;
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
            /*case 5:
                if (this.brickManager != null && brickManager.isLevelComplete() && !this.bossSpawned) {
                    spawnEnemy("BOSS", GameConstants.SCREEN_WIDTH / 2, -GameConstants.BOSS_HEIGHT);
                    this.bossSpawned = true;
                }
                break;*/
        }
        activeEnemies.removeIf(Enemy::isDestroyed);
        for (Enemy enemy : activeEnemies) {
            enemy.update(deltaTime);
        }

        processSpawnQueue();
    }

    private void processSpawnQueue() {
        if (!enemiesToSpawn.isEmpty()) {
            activeEnemies.addAll(enemiesToSpawn);
            enemiesToSpawn.clear();
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

    public boolean isBossDefeated() {
        for (Enemy enemy : activeEnemies) {
            if (enemy instanceof Boss) {
                return false;
            }
        }
        return true;
    }

    public boolean hasBossSpawned() {
        return this.bossSpawned;
    }

    public void updateBossOnly(double deltaTime) {
        processSpawnQueue();
        activeEnemies.removeIf(Enemy::isDestroyed);
        for (Enemy enemy : activeEnemies) {
            if (enemy instanceof Boss) {
                enemy.update(deltaTime);
                break;
            }
        }
    }

    public boolean isBossReady() {
        for (Enemy enemy : activeEnemies) {
            if (enemy instanceof Boss) {
                return enemy.getHasEnteredScreen();
            }
        }
        return false;
    }

    public boolean isBossDying() {
        for (Enemy enemy : activeEnemies) {
            if (enemy instanceof Boss) {
                Boss boss = (Boss) enemy;
                if (boss.getCurrentStrategy() instanceof BossDyingStrategy) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<SavedGameState.EnemyData> getDataToSave() {
        List<SavedGameState.EnemyData> enemyDataList = new ArrayList<>();
        for (Enemy enemy : activeEnemies) {

            enemyDataList.add(new SavedGameState.EnemyData(
                    enemy.getType(),
                    enemy.getX(),
                    enemy.getY(),
                    enemy.getHealth()
            ));
        }
        return enemyDataList;
    }

    public void loadData(List<SavedGameState.EnemyData> enemyDataList) {
        activeEnemies.clear();
        enemiesToSpawn.clear();

        for (SavedGameState.EnemyData data : enemyDataList) {
            Enemy enemy = enemyFactory.createEnemy(data.type, 0, 0);

            if (enemy != null) {

                enemy.setPosition(data.x, data.y);
                enemy.setHealth(data.health);
                enemy.setHasEnteredScreen(true);

                activeEnemies.add(enemy);
            }
        }

        this.bossSpawned = false;
        for (Enemy enemy : activeEnemies) {
            if (enemy instanceof Boss) {
                this.bossSpawned = true;
                break;
            }
        }
    }
}
