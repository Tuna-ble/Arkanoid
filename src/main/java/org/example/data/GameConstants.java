/**
 * Game Constants theo thiết kế logic vật lí của ChatGPT
 */

package org.example.data;

/**
 * Centralized game constants
 * Data layer - provides immutable configuration values
 */
public final class GameConstants {

    // Prevent instantiation
    private GameConstants() {}

    // Screen
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    // Paddle
    public static final int PADDLE_WIDTH = 120;
    public static final int PADDLE_HEIGHT = 16;
    public static final int PADDLE_Y = 536; // SCREEN_HEIGHT - 64
    public static final double PADDLE_SPEED = 600.0; // pixels per second
    public static final double PADDLE_MOVE_INFLUENCE = 0.15; // influence on ball direction

    // Ball
    public static final int BALL_RADIUS = 6;
    public static final double BALL_INITIAL_SPEED = 320.0;
    public static final double BALL_MIN_SPEED = 240.0;
    public static final double BALL_MAX_SPEED = 900.0;
    public static final double BALL_SPEED_INCREMENT_PER_BRICK = 6.0;
    public static final double BALL_MIN_VY = 120.0;
    public static final double BALL_RESTITUTION = 1.0;
    public static final double BALL_MAX_ANGLE_FROM_VERTICAL = 75.0; // degrees

    // Physics / timestep
    public static final double FIXED_TIMESTEP = 0.0083333; // 1/120 seconds
    public static final int MAX_SUBSTEPS = 5;
    public static final double EPSILON = 0.001;

    // Collision / CCD
    public static final boolean USE_CCD = true;
    public static final int MAX_ITER_PER_FRAME = 5;

    // Bricks
    public static final int BRICK_PADDING = 2;
    public static final int BRICK_DURABILITY = 1;

    // Gameplay tuning
    public static final double BALL_INITIAL_ANGLE_RANDOM_RANGE = 15.0; // ±15 degrees
    public static final double BALL_SPEED_SCALE_ON_HIT = 1.02; // speed increase on hit
    public static final double BALL_VERTICAL_CORRECTION_RATIO = 0.3; // force angle if vy too small

    // Calculated constants
    public static final double PADDLE_CENTER_X = SCREEN_WIDTH / 2.0;
    public static final double BALL_INITIAL_X = SCREEN_WIDTH / 2.0;
    public static final double BALL_INITIAL_Y = PADDLE_Y - BALL_RADIUS - 10;
}
