import org.example.gamelogic.entities.bricks.NormalBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NormalBrickTest {
    private NormalBrick brick;

    @BeforeEach
    void SetUp() {
        brick = new NormalBrick(0, 0, 50, 20);
    }

    @Test
    void testTakeDamageNotDestroy() {
        brick.takeDamage(1.0);
        assertTrue(brick.isDestroyed());
    }

    @Test
    void testTakeDamageDestroy() {
        brick.takeDamage(1000);
        assertFalse(brick.isActive());
    }
}
