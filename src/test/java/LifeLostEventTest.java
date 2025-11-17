import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.LifeLostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LifeLostEventTest {
    private EventManager eventManager;

    @BeforeEach
    public void setUp() {
        eventManager = EventManager.getInstance();
    }

    @Test
    public void testLifeLostEventIsPublished() {
        final boolean[] called = {false};

        eventManager.subscribe(LifeLostEvent.class, (LifeLostEvent e) -> {
            called[0] = true;
        });

        LifeLostEvent event = new LifeLostEvent(3);
        eventManager.publish(event);

        assertTrue(called[0], "Listener should be called when event is published");
    }
}
