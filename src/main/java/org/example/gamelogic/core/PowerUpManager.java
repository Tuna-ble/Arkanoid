package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.powerups.ExpandPaddlePowerUp;
import org.example.gamelogic.entities.powerups.FastBallPowerUp;
import org.example.gamelogic.entities.powerups.PowerUp;
import org.example.gamelogic.factory.PowerUpFactory;
import org.example.gamelogic.registry.PowerUpRegistry;
import org.example.gamelogic.strategy.powerup.ExpandPaddleStrategy;
import org.example.gamelogic.strategy.powerup.FastBallStrategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PowerUpManager {
    private static class SingletonHolder {
        private static final PowerUpManager INSTANCE = new PowerUpManager();
    }

    public static PowerUpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final PowerUpFactory powerUpFactory;
    private List<PowerUp> activePowerUps = new ArrayList<>();

    public PowerUpManager() {
        PowerUpRegistry registry = PowerUpRegistry.getInstance();
        registerPowerUpPrototypes(registry);
        this.powerUpFactory = new PowerUpFactory(registry);
    }

    private void registerPowerUpPrototypes(PowerUpRegistry powerUpRegistry) {
        final double POWERUP_WIDTH = 40;
        final double POWERUP_HEIGHT = 40;

        powerUpRegistry.register("E", new ExpandPaddlePowerUp(0.0, 0.0, POWERUP_WIDTH, POWERUP_HEIGHT, 0.0, 3.0, new ExpandPaddleStrategy()));
        powerUpRegistry.register("F", new FastBallPowerUp(0.0, 0.0, POWERUP_WIDTH, POWERUP_HEIGHT, 0.0, 2.0, new FastBallStrategy()));

    }

    public void spawnPowerUp(String type, double x, double y) {
        PowerUp newPowerUp = powerUpFactory.createPowerUp(type, x, y);
        activePowerUps.add(newPowerUp);
    }

    public void update(GameManager gm, double deltaTime) {
        Iterator<PowerUp> iterator = activePowerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.update(deltaTime);
            if (!powerUp.isActive() || powerUp.isOutOfBounds()) {
                iterator.remove();
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (PowerUp powerUp : activePowerUps) {
            powerUp.render(gc);
        }
    }

    public void clear() {
        activePowerUps.clear();
    }

    public List<PowerUp> getActivePowerUps() {
        return activePowerUps;
    }
}
