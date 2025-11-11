import org.example.gamelogic.entities.bricks.UnbreakableBrick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnbreakableBrickTest {

    private UnbreakableBrick brick;

    @BeforeEach
    void setUp() {
        brick = new UnbreakableBrick(10, 20, 50, 20);
    }

    @Test
    void testIsBreakable() {
        assertFalse(brick.isBreakable());
    }

    @Test
    void testTakeDamage() {
        brick.takeDamage(100);
        assertFalse(brick.isDestroyed());
    }
}
