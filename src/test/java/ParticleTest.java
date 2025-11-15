import org.example.gamelogic.entities.Particle;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParticleTest {

    private Particle particle;

    @BeforeEach
    void setUp() {
        particle = new Particle(0, 0, 10, 20, 5, 5, 2.0, Color.RED);
    }

    @Test
    void testUpdate() {
        double deltaTime = 1.0;
        assertEquals(particle.getX() + 10 * deltaTime, particle.getX());
        assertEquals(particle.getY() + 20 * deltaTime, particle.getY());
        assertFalse(particle.isDestroyed());
    }

    @Test
    void testUpdateAfterLifeSpan() {
        particle.update(2.0);
        assertTrue(particle.isDestroyed());
    }
}
