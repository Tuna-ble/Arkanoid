package org.example.gamelogic.entities;

public enum BulletType {
    PLAYER_LASER(4, 20),
    BOSS_HOMING_SQUARE(10, 10),
    BOSS_LASER(8, 40),
    ENEMY_LASER(4, 20);

    public final double width;
    public final double height;

    BulletType(double width, double height) {
        this.width = width;
        this.height = height;
    }
}
