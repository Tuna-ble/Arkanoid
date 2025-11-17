package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.powerups.*;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.factory.PowerUpFactory;
import org.example.gamelogic.registry.PowerUpRegistry;
import org.example.gamelogic.strategy.powerup.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Quản lý power-up: spawn khi gạch bị phá, cập nhật vị trí rớt, render và cung cấp danh sách
 * power-up đang hoạt động.
 */
public final class PowerUpManager {
    private static class SingletonHolder {
        private static final PowerUpManager INSTANCE = new PowerUpManager();
    }

    /**
     * Lấy instance đơn của PowerUpManager.
     *
     * @return singleton PowerUpManager
     */
    public static PowerUpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final PowerUpFactory powerUpFactory;
    private List<PowerUp> activePowerUps = new ArrayList<>();

    private final Random random = new Random();
    private static final double POWERUP_DROP_CHANCE = 0.4;

    private static final String[] POWERUP_TYPES = {"E", "S", "M", "B", "L", "P"};

    public PowerUpManager() {
        PowerUpRegistry registry = PowerUpRegistry.getInstance();
        registerPowerUpPrototypes(registry);
        this.powerUpFactory = new PowerUpFactory(registry);

        subscribeToBrickDestroyedEvent();
    }

    private void subscribeToBrickDestroyedEvent() {
        EventManager.getInstance().subscribe(
                BrickDestroyedEvent.class,
                this::onBrickDestroyed
        );
    }

    private void onBrickDestroyed(BrickDestroyedEvent event) {
        if (random.nextDouble() < POWERUP_DROP_CHANCE) {
            String type = POWERUP_TYPES[random.nextInt(POWERUP_TYPES.length)];

            double x = event.getHitBrick().getX();
            double y = event.getHitBrick().getY();

            spawnPowerUp(type, x, y);
        }
    }

    private void registerPowerUpPrototypes(PowerUpRegistry powerUpRegistry) {
        powerUpRegistry.register("E", new ExpandPaddlePowerUp(0.0, 0.0, GameConstants.POWERUP_WIDTH,
                GameConstants.POWERUP_HEIGHT, 0.0, 2.0, new ExpandPaddleStrategy()));

        powerUpRegistry.register("S", new FastBallPowerUp(0.0, 0.0, GameConstants.POWERUP_WIDTH,
                GameConstants.POWERUP_HEIGHT, 0.0, 2.0, new FastBallStrategy()));

        powerUpRegistry.register("M", new MultiBallPowerUp(0.0, 0.0, GameConstants.POWERUP_WIDTH,
                GameConstants.POWERUP_HEIGHT, 0.0, 2.0, new MultiBallStrategy()));

        powerUpRegistry.register("L", new ExtraLifePowerUp(0.0, 0.0, GameConstants.POWERUP_WIDTH,
                GameConstants.POWERUP_HEIGHT, 0.0, 2.0, new ExtraLifeStrategy()));

        powerUpRegistry.register("B", new LaserPaddlePowerUp(0.0, 0.0, GameConstants.POWERUP_WIDTH,
                GameConstants.POWERUP_HEIGHT, 0.0, 2.0, new LaserPaddleStrategy()));

        powerUpRegistry.register("P", new PiercingBallPowerUp(0.0, 0.0, GameConstants.POWERUP_WIDTH,
                GameConstants.POWERUP_HEIGHT, 0.0, 2.0, new PiercingBallStrategy()));
    }

    /**
     * Tạo một power-up ở vị trí cho trước (thường do gạch bị phá sinh ra).
     *
     * @param type mã loại power-up (ví dụ "E","S","M",...)
     * @param x toạ độ x spawn
     * @param y toạ độ y spawn
     */
    public void spawnPowerUp(String type, double x, double y) {
        PowerUp newPowerUp = powerUpFactory.createPowerUp(type, x, y);
        activePowerUps.add(newPowerUp);
    }

    /**
     * Cập nhật trạng thái các power-up (di chuyển xuống, xử lý hết hạn) và loại bỏ power-up không còn hoạt động.
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước
     */
    public void update(double deltaTime) {
        Iterator<PowerUp> iterator = activePowerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.update(deltaTime);
            if (!powerUp.isActive() || powerUp.isOutOfBounds()) {
                iterator.remove();
            }
        }
    }

    /**
     * Vẽ tất cả power-up lên canvas.
     *
     * @param gc GraphicsContext của canvas (kỳ vọng không null)
     */
    public void render(GraphicsContext gc) {
        for (PowerUp powerUp : activePowerUps) {
            powerUp.render(gc);
        }
    }

    /**
     * Xóa mọi power-up đang tồn tại.
     */
    public void clear() {
        activePowerUps.clear();
    }

    /**
     * Lấy danh sách các power-up đang hoạt động.
     *
     * @return danh sách PowerUp (mutable)
     */
    public List<PowerUp> getActivePowerUps() {
        return activePowerUps;
    }
}
