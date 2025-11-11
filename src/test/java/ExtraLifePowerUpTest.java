import org.example.gamelogic.entities.powerups.ExtraLifePowerUp;
import org.example.gamelogic.strategy.powerup.ExtraLifeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExtraLifePowerUpTest {

    private ExtraLifePowerUp powerUp;

    @BeforeEach
    void setUp() {
        powerUp = new ExtraLifePowerUp(10, 20, 30, 30, 0, 0, new ExtraLifeStrategy());
    }

    @Test
    void testClone() {
        ExtraLifePowerUp clone2 = (ExtraLifePowerUp) powerUp.clone();
        assertEquals(powerUp.getWidth(), clone2.getWidth());
        assertEquals(powerUp.getHeight(), clone2.getHeight());
    }
    @Test
    void testGetSpriteRow() {
        assertEquals(5, powerUp.getSpriteRow());
    }
}
