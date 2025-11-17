import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.entities.bricks.Brick;
import org.example.gamelogic.entities.bricks.NormalBrick;
import org.example.gamelogic.events.BrickDestroyedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BrickDestroyedEventTest {
    private EventManager eventManager;

    @BeforeEach
    public void setUp() {
        eventManager = EventManager.getInstance();
    }

    @Test
    public void testBrickDestroyedEventIsPublished() {
        final boolean[] called = {false};

        eventManager.subscribe(BrickDestroyedEvent.class, (BrickDestroyedEvent e) -> {
            called[0] = true;
        });

        Brick dummyBrick = new NormalBrick(0, 0, 50, 20);
        BrickDestroyedEvent event = new BrickDestroyedEvent(dummyBrick);

        eventManager.publish(event);

        assertTrue(called[0], "Listener should be called when event is published");
    }
}

