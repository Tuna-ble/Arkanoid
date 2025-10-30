package org.example.config;
/**
 * Game Constants
 */

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public final class GameConstants {

    public static final Color BALL_COLOR = Color.RED;
    public static final Color PADDLE_COLOR = Color.CYAN;
    public static final Color PADDLE_BORDER_COLOR = Color.DARKBLUE;
    public static final int POINTS_PER_EXPLOSIVE_BRICK = 30;

    // Prevent instantiation
    private GameConstants() {}

    // Screen
    public static final double SCREEN_WIDTH = 800;
    public static final double SCREEN_HEIGHT = 600;

    // Paddle
    public static final double PADDLE_X = SCREEN_WIDTH / 2;
    public static final double PADDLE_Y = 536; // SCREEN_HEIGHT - 64
    public static final double PADDLE_WIDTH = 120;
    public static final double PADDLE_HEIGHT = 16;
    public static final double PADDLE_SPEED = 600.0; // pixels per second
    public static final double PADDLE_MOVE_INFLUENCE = 0.15; // influence on ball direction

    // Ball
    public static final double BALL_RADIUS = 8;
    public static final double BALL_INITIAL_SPEED = 320.0;
    public static final double BALL_MIN_SPEED = 240.0;
    public static final double BALL_MAX_SPEED = 900.0;
    public static final double BALL_SPEED_INCREMENT_PER_BRICK = 6.0;
    public static final double BALL_MIN_VY = 120.0;
    public static final double BALL_RESTITUTION = 1.0;
    public static final double BALL_MAX_ANGLE_FROM_VERTICAL = 75.0; // degrees

    // Physics / timestep
    public static final double FIXED_TIMESTEP = 0.0083333; // 1/120 seconds
    public static final double MAX_SUBSTEPS = 5;
    public static final double EPSILON = 0.001;

    // Collision / CCD
    public static final boolean USE_CCD = true;
    public static final double MAX_ITER_PER_FRAME = 5;

    // Bricks
    public static final double BRICK_PADDING = 2;
    public static final double BRICK_DURABILITY = 1;
    public static final double BRICK_WIDTH = 60;
    public static final double BRICK_HEIGHT = 60;
    public static final double PADDING = 5;
    public static final double TOP_MARGIN = 50;
    public static final int HARD_BRICK_DURABILITY = 3;
    public static final int POINTS_PER_HARD_BRICK = 50;

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

}