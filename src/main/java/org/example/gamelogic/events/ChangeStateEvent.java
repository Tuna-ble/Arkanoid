package org.example.gamelogic.events;

import org.example.gamelogic.states.GameState;
import org.example.gamelogic.states.GameStateEnum;

public class ChangeStateEvent extends GameEvent {
    public final GameStateEnum targetState;;
    public final Object payload;

    public ChangeStateEvent(GameStateEnum state) {
        this.targetState = state;
        this.payload = null;
    }

    public GameStateEnum getTargetState() {
        return targetState;
    }

    public ChangeStateEvent(GameStateEnum state, Object payload) {
        this.targetState = state;
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}
