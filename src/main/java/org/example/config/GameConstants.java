package org.example.config;
/**
 * Game Constants theo thiết kế logic vật lí của ChatGPT
 */

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Centralized game constants
 * Data layer - provides immutable configuration values
 */
public final class GameConstants {

    // Color
    public static final Color BALL_COLOR = Color.RED;
    public static final Color PADDLE_COLOR = Color.CYAN;
    public static final Color PADDLE_BORDER_COLOR = Color.DARKBLUE;

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
    public static final double PADDLE_SPEED = 400.0; // pixels per second

    // Ball
    public static final double BALL_RADIUS = 8;
    public static final double BALL_INITIAL_SPEED = 320.0;
    public static final double BALL_MIN_SPEED = 240.0;
    public static final double BALL_MAX_SPEED = 900.0;
    public static final double BALL_SPEED_INCREMENT_PER_BRICK = 6.0;
    public static final double BALL_MIN_VY = 120.0;
    public static final double BALL_INITIAL_ANGLE_RANDOM_RANGE = 20.0; // ±15 degrees


    // Bricks
    public static final double BRICK_WIDTH = 60;
    public static final double BRICK_HEIGHT = 60;
    public static final double PADDING = 5;
    public static final double TOP_MARGIN = 50;

    // Gameplay tuning

}