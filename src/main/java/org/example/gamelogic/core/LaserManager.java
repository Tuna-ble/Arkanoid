package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.entities.LaserBullet;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.events.BrickDamagedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class LaserManager {
    private static class SingletonHolder {
        private static final LaserManager INSTANCE = new LaserManager();
    }

    public static LaserManager getInstance() {
        return LaserManager.SingletonHolder.INSTANCE;
    }

    private final List<LaserBullet> lasers=new ArrayList<>();

    public void shoot(Paddle paddle) {
        double leftX=paddle.getX()+10;
        double rightX= paddle.getX()+ paddle.getWidth()-14;
        double y= paddle.getY()-16;

        lasers.add(new LaserBullet(leftX, y));
        lasers.add(new LaserBullet(rightX, y));
    }

    public void update(double deltaTime) {
        Iterator<LaserBullet> iterator= lasers.iterator();
        while (iterator.hasNext()) {
            LaserBullet laser=iterator.next();
            laser.update(deltaTime);

            if (!laser.isActive()) {
                iterator.remove();
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (LaserBullet laser : lasers) {
            laser.render(gc);
        }
    }

    public List<LaserBullet> getLasers() {
        return lasers;
    }

    public void clear() {
        lasers.clear();
    }
}
