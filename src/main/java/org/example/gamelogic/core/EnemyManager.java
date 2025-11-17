package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.enemy.*;
import org.example.gamelogic.factory.EnemyFactory;
import org.example.gamelogic.registry.EnemyRegistry;
import org.example.gamelogic.states.GameModeEnum;
import org.example.gamelogic.strategy.bossbehavior.BossDyingStrategy;
import org.example.data.SavedGameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý kẻ địch: tạo, spawn, cập nhật, render và lưu/khôi phục trạng thái kẻ địch.
 *
 * <p>Cung cấp API để spawn kẻ địch theo loại, cập nhật luồng spawn cho từng chế độ chơi
 * và truy xuất danh sách kẻ địch đang hoạt động.
 */
public final class EnemyManager {
    private static class SingletonHolder {
        private static final EnemyManager INSTANCE = new EnemyManager();
    }

    /**
     * Lấy instance đơn của EnemyManager.
     *
     * @return singleton EnemyManager
     */
    public static EnemyManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final EnemyFactory enemyFactory;
    private List<Enemy> activeEnemies = new ArrayList<>();
    private List<Enemy> enemiesToSpawn = new ArrayList<>();

    private GameManager gameManager;
    private BrickManager brickManager;

    private static final String[] ENEMY_TYPES = {"E1", "E2"};

    private GameModeEnum currentGameMode;
    private double levelTimer = 0.0;
    private int enemiesSpawned = 0;
    private final int MAX_ENEMIES_PER_LEVEL = 5;
    private int currentLevelNumber;
    private boolean bossSpawned;

    /**
     * Khởi tạo EnemyManager: đăng ký prototype và khởi tạo factory.
     */
    public EnemyManager() {
        EnemyRegistry registry = EnemyRegistry.getInstance();
        registerEnemyPrototypes(registry);
        this.enemyFactory = new EnemyFactory(registry);
        this.bossSpawned = false;
    }

    /**
     * Đăng ký prototype cho các loại enemy vào registry (dùng bởi EnemyFactory sau này).
     *
     * @param enemyRegistry registry để đăng ký prototype
     */
    private void registerEnemyPrototypes(EnemyRegistry enemyRegistry) {
    enemyRegistry.register("E1", new Enemy1(0.0, 0.0, GameConstants.ENEMY_WIDTH,
        GameConstants.ENEMY_HEIGHT, 25.0, 50.0));
    enemyRegistry.register("E2", new Enemy2(0.0, 0.0, GameConstants.ENEMY_WIDTH,
        GameConstants.ENEMY_HEIGHT, 25.0, 50.0));
    enemyRegistry.register("MINION", new BossMinion(0.0, 0.0, GameConstants.MINION_WIDTH,
        GameConstants.MINION_HEIGHT, 25.0, 50.0));
    enemyRegistry.register("BOSS", new Boss(0, 0, 0, 100));
    }

    /**
     * Spawn một kẻ địch cụ thể tại toạ độ center.
     *
     * @param type mã loại kẻ địch (ví dụ "E1", "BOSS")
     * @param centerX toạ độ x trung tâm spawn
     * @param centerY toạ độ y trung tâm spawn
     */
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

    /**
     * Spawn một kẻ địch ở vị trí bắt đầu ngẫu nhiên phía trên màn hình.
     *
     * @param type mã loại kẻ địch
     */
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

    /**
     * Chuẩn bị script level (thiết lập chế độ chơi và số level để spawn enemy).
     *
     * @param currentGameMode chế độ chơi hiện tại
     * @param levelNumber số level hiện tại
     */
    public void loadLevelScript(GameModeEnum currentGameMode, int levelNumber) {
        this.currentGameMode = currentGameMode;
        this.currentLevelNumber = levelNumber;
        this.levelTimer = 0.0;
        this.enemiesSpawned = 0;

        this.bossSpawned = false;
        this.activeEnemies.clear();
    }

    /**
     * Đặt tham chiếu đến BrickManager (nếu cần kiểm tra trạng thái gạch khi spawn boss).
     *
     * @param brickManager instance của BrickManager
     */
    public void setBrickManager(BrickManager brickManager) {
        this.brickManager = brickManager;
    }

    /**
     * Cập nhật trạng thái kẻ địch, xử lý spawn theo script/điều kiện.
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước
     */
    public void update(double deltaTime) {
        levelTimer += deltaTime;

        if (currentGameMode == GameModeEnum.LEVEL) {
            updateLevelMode();
        } else if (currentGameMode == GameModeEnum.INFINITE) {
            updateInfiniteMode();
        }

        activeEnemies.removeIf(Enemy::isDestroyed);
        for (Enemy enemy : activeEnemies) {
            enemy.update(deltaTime);
        }

        processSpawnQueue();
    }

    /**
     * Cập nhật logic spawn dành cho chế độ LEVEL (theo số level cụ thể).
     */
    private void updateLevelMode() {
        switch (currentLevelNumber) {
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
    }

    /**
     * Cập nhật logic spawn dành cho chế độ INFINITE.
     */
    private void updateInfiniteMode() {
        if (currentLevelNumber % 2 == 0 && currentLevelNumber % 10 != 0) {
            if (levelTimer > 5.0 && enemiesSpawned < 10) {
                levelTimer = 0.0;
                enemiesSpawned++;
                if (Math.random() < 0.5) {
                    spawnEnemy("E1");
                } else {
                    spawnEnemy("E2");
                }
            }
        }
    }

    /**
     * Xử lý hàng chờ spawn: chuyển các enemy đã sinh vào danh sách active.
     */
    private void processSpawnQueue() {
        if (!enemiesToSpawn.isEmpty()) {
            activeEnemies.addAll(enemiesToSpawn);
            enemiesToSpawn.clear();
        }
    }

    /**
     * Vẽ tất cả kẻ địch hiện hoạt lên canvas.
     *
     * @param gc GraphicsContext của canvas (kỳ vọng không null)
     */
    public void render(GraphicsContext gc) {
        for (Enemy enemy : activeEnemies) {
            enemy.render(gc);
        }
    }

    /**
     * Xóa danh sách kẻ địch đang hoạt động.
     */
    public void clear() {
        activeEnemies.clear();
    }

    /**
     * Lấy danh sách kẻ địch đang hoạt động.
     *
     * @return danh sách Enemy (mutable)
     */
    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    /**
     * Kiểm tra xem boss còn tồn tại trong danh sách kẻ địch hay không.
     *
     * @return true nếu không còn boss; false nếu vẫn còn
     */
    public boolean isBossDefeated() {
        for (Enemy enemy : activeEnemies) {
            if (enemy instanceof Boss) {
                return false;
            }
        }
        return true;
    }

    /**
     * Kiểm tra đã từng spawn boss trong level hiện tại chưa.
     *
     * @return true nếu boss đã spawn; false nếu chưa
     */
    public boolean hasBossSpawned() {
        return this.bossSpawned;
    }

    /**
     * Cập nhật chỉ cho boss (dùng khi chỉ boss cần được xử lý).
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước
     */
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

    /**
     * Kiểm tra boss đã vào màn hình (ready) chưa.
     *
     * @return true nếu boss đã vào màn hình; false ngược lại
     */
    public boolean isBossReady() {
        for (Enemy enemy : activeEnemies) {
            if (enemy instanceof Boss) {
                return enemy.getHasEnteredScreen();
            }
        }
        return false;
    }

    /**
     * Kiểm tra boss đang ở trạng thái dying (theo strategy).
     *
     * @return true nếu boss đang chết; false nếu không
     */
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

    /**
     * Chuẩn bị dữ liệu kẻ địch để lưu trạng thái trò chơi.
     *
     * @return danh sách EnemyData chứa type, vị trí và máu của từng kẻ địch
     */
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

    /**
     * Khôi phục kẻ địch từ dữ liệu đã lưu.
     *
     * @param enemyDataList danh sách dữ liệu kẻ địch đã lưu
     */
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
