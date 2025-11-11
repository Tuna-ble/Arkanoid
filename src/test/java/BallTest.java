import org.example.gamelogic.entities.Ball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BallTest {
    private Ball ball;

    @BeforeEach
    void setUp() {
        ball = new Ball(100, 200, 10);
    }

    @Test
    void testResetPositions() {
        ball.reset(200, 300, 100);
        assertTrue(ball.isAttachedToPaddle());
        assertEquals(200 + 50 - 10, ball.getX(), 0.001);
        assertEquals(300 - 20, ball.getY(), 0.001);
    }

    @Test
    void testDestroy() {
        assertTrue(ball.isActive());
        ball.destroy();
        assertTrue(ball.isDestroyed());
    }
}
