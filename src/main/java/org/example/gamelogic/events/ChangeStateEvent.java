package org.example.gamelogic.events;

import org.example.gamelogic.states.GameState;
import org.example.gamelogic.states.GameStateEnum;

public class ChangeStateEvent extends GameEvent {
    public final GameStateEnum targetState;;

    public ChangeStateEvent(GameStateEnum state) {
        this.targetState = state;
    }

    public GameStateEnum getTargetState() {
        return targetState;
    }
}
