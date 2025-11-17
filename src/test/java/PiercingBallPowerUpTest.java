import org.example.gamelogic.entities.powerups.PiercingBallPowerUp;
import org.example.gamelogic.strategy.powerup.ExtraLifeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PiercingBallPowerUpTest {

    private PiercingBallPowerUp powerUp;

    @BeforeEach
    void setUp() {
        powerUp = new PiercingBallPowerUp(10, 20, 30, 30, 0, 0, new ExtraLifeStrategy());
    }

    @Test
    void testClone() {
        PiercingBallPowerUp clone2 = (PiercingBallPowerUp) powerUp.clone();
        assertEquals(powerUp.getWidth(), clone2.getWidth());
        assertEquals(powerUp.getHeight(), clone2.getHeight());
    }

    @Test
    void testGetSpriteRow() {
        assertEquals(7, powerUp.getSpriteRow());
    }
}
