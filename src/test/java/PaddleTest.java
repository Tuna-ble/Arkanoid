import org.example.gamelogic.entities.Paddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaddleTest {

    private Paddle paddle;

    @BeforeEach
    void setUp() {
        paddle = new Paddle(100, 400, 80, 20, 0, 0);
    }

    @Test
    void testMoveLeft() {
        paddle.moveLeft();
        assertTrue(paddle.getDx() < 0);
    }

    @Test
    void testMoveRight() {
        paddle.moveRight();
        assertTrue(paddle.getDx() > 0);
    }
}
