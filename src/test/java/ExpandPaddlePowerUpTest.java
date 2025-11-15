import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.entities.powerups.ExpandPaddlePowerUp;
import org.example.gamelogic.strategy.powerup.ExpandPaddleStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpandPaddlePowerUpTest {
    private ExpandPaddlePowerUp powerUp;
    private Paddle paddle;

    @BeforeEach
    void setUp() {
        powerUp = new ExpandPaddlePowerUp(0, 0, 20, 10, 0, 0, new ExpandPaddleStrategy());
        paddle = new Paddle(50, 100, 40, 10, 0, 0);
    }

    @Test
    void testApplyStrategy() {
        double oldWidth = paddle.getWidth();
        paddle.setWidth(paddle.getWidth() * 1.5);
        assertTrue(paddle.getWidth() > oldWidth);
    }

    @Test
    void testGetSpriteRow() {
        assertEquals(3, powerUp.getSpriteRow());
    }
}
