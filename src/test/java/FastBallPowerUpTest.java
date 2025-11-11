import org.example.gamelogic.entities.powerups.FastBallPowerUp;
import org.example.gamelogic.strategy.powerup.FastBallStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FastBallPowerUpTest {
    private FastBallPowerUp powerUp;

    @BeforeEach
    void setUp() {
        powerUp = new FastBallPowerUp(10, 20, 30, 30, 0, 0, new FastBallStrategy());
    }

    @Test
    void testClone() {
        FastBallPowerUp clone2 = (FastBallPowerUp) powerUp.clone();
        assertEquals(powerUp.getWidth(), clone2.getWidth());
        assertEquals(powerUp.getHeight(), clone2.getHeight());
    }

    @Test
    void testGetSpriteRow() {
        assertEquals(8, powerUp.getSpriteRow());
    }
}
