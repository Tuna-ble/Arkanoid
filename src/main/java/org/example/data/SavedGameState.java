package org.example.data;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class SavedGameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public int levelId;
    public int score;
    public int lives;

    public double paddleX;
    public double paddleY;
    public double paddleWidth;

    public List<BallData> balls = new ArrayList<>();
    public List<BrickData> bricks = new ArrayList<>();
    public List<EnemyData> enemies = new ArrayList<>();

    public static class BallData implements Serializable {
        private static final long serialVersionUID = 1L;
        public double x, y;
        public double vx, vy;

        public BallData() {}

        /**
         * Tạo dữ liệu bóng với vị trí và vận tốc.
         *
         * @param x  vị trí X
         * @param y  vị trí Y
         * @param vx vận tốc X
         * @param vy vận tốc Y
         */
        public BallData(double x, double y, double vx, double vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }
    }

    public static class BrickData implements Serializable {
        private static final long serialVersionUID = 1L;

        public int id;
        public int health;
        public boolean isDestroyed;

        public BrickData() {}

        /**
         * Tạo dữ liệu gạch với thông tin trạng thái.
         *
         * @param id         mã loại gạch
         * @param health     máu còn lại
         * @param isDestroyed trạng thái gạch đã bị phá hay chưa
         */
        public BrickData(int id, int health, boolean isDestroyed) {
            this.id = id;
            this.health = health;
            this.isDestroyed = isDestroyed;
        }
    }

    public static class EnemyData implements Serializable {
        private static final long serialVersionUID = 1L;

        public String type;
        public double x, y;
        public int health;

        public EnemyData() {}

        /**
         * Tạo dữ liệu kẻ địch với vị trí, loại và máu.
         *
         * @param type   loại enemy (string key)
         * @param x      vị trí X
         * @param y      vị trí Y
         * @param health lượng máu còn lại
         */
        public EnemyData(String type, double x, double y, int health) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.health = health;
        }
    }
}