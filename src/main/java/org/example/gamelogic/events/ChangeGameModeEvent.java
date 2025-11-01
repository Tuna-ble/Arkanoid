package org.example.gamelogic.events;

import org.example.gamelogic.states.GameModeEnum;

public final class ChangeGameModeEvent extends GameEvent {
    private final GameModeEnum targetMode;

    public ChangeGameModeEvent(GameModeEnum mode) {
        this.targetMode=mode;
    }

    public GameModeEnum getTargetMode() {
        return targetMode;
    }
}
