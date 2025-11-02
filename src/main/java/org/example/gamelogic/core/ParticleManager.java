package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.gamelogic.entities.Particle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class ParticleManager {

    private final List<Particle> particles;
    private final Random random;

    private ParticleManager() {
        this.particles = new ArrayList<>();
        this.random = new Random();
    }

    private static class SingletonHolder {
        private static final ParticleManager INSTANCE = new ParticleManager();
    }

    public static ParticleManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void update(double deltaTime) {

        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update(deltaTime);

            if (p.isDestroyed()) {
                iterator.remove();
            }
        }
    }

    public void render(GraphicsContext gc) {

        for (Particle p : particles) {
            p.render(gc);
        }
    }

    public void spawnBrickDebris(double x, double y, Color color) {
        int particleCount = 20;
        double particleSize = 4;
        double maxSpeed = 300.0;
        double maxLife = 0.8;

        for (int i = 0; i < particleCount; i++) {
            double dx = (random.nextDouble() - 0.5) * maxSpeed;

            double dy = (random.nextDouble() * -1.0) * maxSpeed;

            double life = maxLife * (0.5 + random.nextDouble() * 0.5);

            Particle p = new Particle(
                    x, y, dx, dy,
                    particleSize, particleSize,
                    life, color
            );
            particles.add(p);
        }
    }


    public void clear() {
        particles.clear();
    }
}