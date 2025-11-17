package org.example.gamelogic.events;

import org.example.gamelogic.states.GameStateEnum;

public final class ChangeStateEvent extends GameEvent {
    public final GameStateEnum targetState;
    public final Object payload;

    /**
     * Tạo event yêu cầu chuyển sang state mới, không kèm dữ liệu bổ sung.
     *
     * @param state state mục tiêu cần chuyển đến
     */
    public ChangeStateEvent(GameStateEnum state) {
        this.targetState = state;
        this.payload = null;
    }

    /**
     * @return state mục tiêu cần chuyển đến
     */
    public GameStateEnum getTargetState() {
        return targetState;
    }

    /**
     * Tạo event yêu cầu chuyển state, kèm payload tùy chọn.
     *
     * @param state   state mục tiêu
     * @param payload dữ liệu gửi kèm (có thể null)
     */
    public ChangeStateEvent(GameStateEnum state, Object payload) {
        this.targetState = state;
        this.payload = payload;
    }

    /**
     * @return payload đính kèm event (có thể null)
     */
    public Object getPayload() {
        return payload;
    }
}
