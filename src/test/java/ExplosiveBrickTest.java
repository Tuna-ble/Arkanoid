import org.example.gamelogic.entities.bricks.ExplosiveBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExplosiveBrickTest {

    private ExplosiveBrick brick;

    @BeforeEach
    void setUp() {
        brick = new ExplosiveBrick(10, 20, 50, 20);
    }

    @Test
    void testTakeDamage() {
        brick.takeDamage(1);
        assertTrue(brick.isDestroyed());
    }

    @Test
    void testClone() {
        ExplosiveBrick clone2 = (ExplosiveBrick) brick.clone();
        assertEquals(brick.getWidth(), clone2.getWidth());
        assertEquals(brick.getHeight(), clone2.getHeight());
    }
}
