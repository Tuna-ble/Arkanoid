package org.example.config;
/**
 * Game Constants
 */

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public final class GameConstants {

    // Prevent instantiation
    private GameConstants() {}

    // Screen
    public static final double SCREEN_WIDTH = 900;
    public static final double SCREEN_HEIGHT = 750;

    // UI
    public static final double FRAME_TOP_BORDER = 25;
    public static final double FRAME_LEFT_BORDER = 25;
    public static final double FRAME_RIGHT_BORDER = 25;
    public static final double FRAME_BOTTOM_BORDER = 25;
    public static final double UI_BAR_WIDTH = 200;
    public static final double UI_BAR_HEIGHT = 0;

    public static final double PLAY_AREA_X = FRAME_LEFT_BORDER;
    public static final double PLAY_AREA_Y = FRAME_TOP_BORDER;

    public static final double PLAY_AREA_WIDTH = SCREEN_WIDTH - FRAME_LEFT_BORDER - FRAME_RIGHT_BORDER - UI_BAR_WIDTH;
    public static final double PLAY_AREA_HEIGHT = SCREEN_HEIGHT - FRAME_TOP_BORDER - FRAME_BOTTOM_BORDER;

    // Color
    public static final Color NORMAL_BALL_COLOR = Color.RED;
    public static final Color PIERCING_BALL_COLOR = Color.CYAN;
    public static final Color PADDLE_COLOR = Color.CYAN;
    public static final Color PADDLE_BORDER_COLOR = Color.DARKBLUE;

    // Paddle
    public static final double PADDLE_X = SCREEN_WIDTH / 2;
    public static final double PADDLE_Y = 536; // SCREEN_HEIGHT - 64
    public static final double PADDLE_WIDTH = 120;
    public static final double PADDLE_HEIGHT = 16;
    public static final double PADDLE_SPEED = 600.0; // pixels per second
    public static final double PADDLE_MOVE_INFLUENCE = 0.7; // influence on ball direction
    public static final double PADDLE_LENGTH_CAP = 180;

    // Ball
    public static final double BALL_RADIUS = 8;
    public static final double BALL_INITIAL_SPEED = 320.0;
    public static final double BALL_MIN_SPEED = 240.0;
    public static final double BALL_MAX_SPEED = 900.0;
    public static final double BALL_SPEED_INCREMENT_PER_BRICK = 6.0;
    public static final double BALL_MIN_VY = 120.0;
    public static final double BALL_RESTITUTION = 1.0;
    public static final double BALL_MAX_ANGLE_FROM_VERTICAL = 75.0; // degrees
    public static final int MAX_BALL_COUNT = 30;

    // Physics / timestep
    public static final double FIXED_TIMESTEP = 0.0083333; // 1/120 seconds
    public static final double MAX_SUBSTEPS = 5;
    public static final double EPSILON = 0.001;

    // Collision / CCD
    public static final boolean USE_CCD = true;
    public static final double MAX_ITER_PER_FRAME = 5;
    public static final double COLLISION_EPSILON = 0.1;

    // Bricks
    public static final double BRICK_PADDING = 2;
    public static final double BRICK_DURABILITY = 1;
    public static final double BRICK_WIDTH = 50;
    public static final double BRICK_HEIGHT = 20;
    public static final double PADDING = 5;
    public static final double TOP_MARGIN = 50;
    public static final double HARD_BRICK_DURABILITY = 3;
    public static final int POINTS_PER_HARD_BRICK = 50;
    public static final int POINTS_PER_EXPLOSIVE_BRICK = 30;

    // Enemies
    public static final double ENEMY_WIDTH = 40;
    public static final double ENEMY_HEIGHT = 40;
    public static final double BOSS_WIDTH = 200;
    public static final double BOSS_HEIGHT = 100;
    public static final double BOSS_HEATLTH = 10.0;

    // Gameplay tuning
    public static final double BALL_INITIAL_ANGLE_RANDOM_RANGE = 15.0; // ±15 degrees
    public static final double BALL_SPEED_SCALE_ON_HIT = 1.02; // speed increase on hit
    public static final double BALL_VERTICAL_CORRECTION_RATIO = 0.3; // force angle if vy too small

    // Calculated constants
    // Removed unused calculated position constants (were not referenced in code)

    // Score
    public static final int POINTS_PER_BRICK = 10;

    // UI - Buttons
    public static final double UI_BUTTON_WIDTH = 200;
    public static final double UI_BUTTON_HEIGHT = 60;
    public static final double UI_BUTTON_SPACING = 20;

    // Lives
    public static final int INITIAL_LIVES = 3;

    // Damages
    public static final double BALL_DAMAGE = 1;
    public static final double EXPLOSIVE_BRICK_DAMAGE = 2;
    public static final double LASER_BULLET_DAMAGE = 0.5;
}
