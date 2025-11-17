package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.example.gamelogic.entities.Particle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Quản lý các particle dùng cho hiệu ứng (ví dụ mảnh vụn gạch).
 *
 * <p>Singleton; cung cấp API để spawn, cập nhật, render và xóa particle.
 */
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

    /**
     * Lấy instance đơn của ParticleManager.
     *
     * @return singleton ParticleManager
     */
    public static ParticleManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Cập nhật trạng thái tất cả particle và loại bỏ những particle đã hỏng.
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước
     */
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

    /**
     * Vẽ tất cả particle lên canvas.
     *
     * @param gc GraphicsContext (kỳ vọng không null)
     */
    public void render(GraphicsContext gc) {

        for (Particle p : particles) {
            p.render(gc);
        }
    }

    /**
     * Sinh hiệu ứng mảnh vụn khi một viên gạch bị phá.
     *
     * @param x toạ độ x trung tâm vụn
     * @param y toạ độ y trung tâm vụn
     * @param color màu của mảnh vụn
     */
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


    /**
     * Xóa tất cả particle.
     */
    public void clear() {
        particles.clear();
    }
}