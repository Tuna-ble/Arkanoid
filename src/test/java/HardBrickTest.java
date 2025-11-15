import org.example.gamelogic.entities.bricks.HardBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HardBrickTest {

    private HardBrick brick;

    @BeforeEach
    void setUp() {
        brick = new HardBrick(10, 20, 50, 20);
    }

    @Test
    void testTakeDamage() {
        brick.takeDamage(1);
        assertFalse(brick.isDestroyed());
        brick.takeDamage(1000);
        assertTrue(brick.isDestroyed());
    }

    @Test
    void testClone() {
        HardBrick clone2 = (HardBrick) brick.clone();
        assertEquals(brick.getWidth(), clone2.getWidth());
        assertEquals(brick.getHeight(), clone2.getHeight());
    }
}
