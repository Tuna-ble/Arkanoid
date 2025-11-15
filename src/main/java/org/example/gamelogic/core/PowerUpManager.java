package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.powerups.*;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.example.gamelogic.factory.PowerUpFactory;
import org.example.gamelogic.registry.PowerUpRegistry;
import org.example.gamelogic.strategy.powerup.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class PowerUpManager {
    private static class SingletonHolder {
        private static final PowerUpManager INSTANCE = new PowerUpManager();
    }

    public static PowerUpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final PowerUpFactory powerUpFactory;
    private List<PowerUp> activePowerUps = new ArrayList<>();

    private final Random random = new Random();
    private static final double POWERUP_DROP_CHANCE = 0.4;

    private static final String[] POWERUP_TYPES = {"E", "F", "M", "H", "L", "P"};
    //private static final String[] POWERUP_TYPES = {"L", "L", "L", "L", "L", "L"};

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
        final double POWERUP_WIDTH = 40;
        final double POWERUP_HEIGHT = 40;

        powerUpRegistry.register("E", new ExpandPaddlePowerUp(0.0, 0.0, POWERUP_WIDTH,
                POWERUP_HEIGHT, 0.0, 2.0, new ExpandPaddleStrategy()));

        powerUpRegistry.register("F", new FastBallPowerUp(0.0, 0.0, POWERUP_WIDTH,
                POWERUP_HEIGHT, 0.0, 2.0, new FastBallStrategy()));

        powerUpRegistry.register("M", new MultiBallPowerUp(0.0, 0.0, POWERUP_WIDTH,
                POWERUP_HEIGHT, 0.0, 2.0, new MultiBallStrategy()));

        powerUpRegistry.register("H", new ExtraLifePowerUp(0.0, 0.0, POWERUP_WIDTH,
                POWERUP_HEIGHT, 0.0, 2.0, new ExtraLifeStrategy()));

        powerUpRegistry.register("L", new LaserPaddlePowerUp(0.0, 0.0, POWERUP_WIDTH,
                POWERUP_HEIGHT, 0.0, 2.0, new LaserPaddleStrategy()));

        powerUpRegistry.register("P", new PiercingBallPowerUp(0.0, 0.0, POWERUP_WIDTH,
                POWERUP_HEIGHT, 0.0, 2.0, new PiercingBallStrategy()));
    }

    public void spawnPowerUp(String type, double x, double y) {
        PowerUp newPowerUp = powerUpFactory.createPowerUp(type, x, y);
        activePowerUps.add(newPowerUp);
    }

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
