import org.example.config.GameConstants;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.ScoreManager;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.bricks.NormalBrick;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreManagerTest {
    private ScoreManager scoreManager;
    private EventManager eventManager;

    @BeforeEach
    public void setup() {
        eventManager = EventManager.getInstance();
        scoreManager = ScoreManager.getInstance();
    }

    @Test
    public void testScoreIncreasesOnNormalBrickDestroyed() {
        Brick normal = new NormalBrick(0, 0, 50, 20);
        BrickDestroyedEvent event = new BrickDestroyedEvent(normal);

        eventManager.publish(event);

        assertEquals(GameConstants.POINTS_PER_BRICK, scoreManager.getScore());
    }
}
