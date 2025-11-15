import org.example.gamelogic.entities.bricks.HealingBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HealingBrickTest {

    private HealingBrick brick;

    @BeforeEach
    void setUp() {
        brick = new HealingBrick(10, 20, 50, 20);
    }

    @Test
    void testTakeDamageFirstHit() {
        brick.takeDamage(1);
        assertFalse(brick.isDestroyed());
    }

    @Test
    void testTakeDamageSecondHit() {
        brick.takeDamage(1);
        brick.takeDamage(1);
        assertTrue(brick.isDestroyed());
    }
}
