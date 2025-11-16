package org.example.gamelogic.entities;

public enum BulletType {
    PLAYER_LASER(7, 20),
    BOSS_HOMING_SQUARE(15, 15),
    BOSS_LASER(20, 60),
    ENEMY_LASER(4, 20);

    public final double width;
    public final double height;

    BulletType(double width, double height) {
        this.width = width;
        this.height = height;
    }
}
