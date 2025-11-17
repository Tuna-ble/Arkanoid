package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.states.GameState;
import org.example.presentation.InputHandler;

/**
 * Quản lý trạng thái game hiện tại (GameState) và chuyển tiếp các cuộc gọi update/render/input.
 *
 * <p>Singleton; các method cho phép set/get state và chuyển tiếp việc cập nhật & render.
 */
public final class StateManager {
    private GameState currentState;

    private static class SingletonHolder {
        private static final StateManager INSTANCE = new StateManager();
    }

    /**
     * Lấy instance đơn của StateManager.
     *
     * @return singleton StateManager
     */
    public static StateManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Đặt trạng thái game hiện tại.
     *
     * @param newState instance GameState mới (có thể null)
     */
    public void setState(GameState newState) {
        this.currentState = newState;
    }

    /**
     * Lấy trạng thái game hiện tại.
     *
     * @return current GameState (có thể null)
     */
    public GameState getState() {
        return this.currentState;
    }

    /**
     * Chuyển tiếp cập nhật tới state hiện tại (nếu có).
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước
     */
    public void update(double deltaTime) {
        if (currentState != null)
            currentState.update(deltaTime);
    }

    /**
     * Chuyển tiếp vẽ (render) tới state hiện tại (nếu có).
     *
     * @param gc GraphicsContext để vẽ
     */
    public void render(GraphicsContext gc) {
        if (currentState != null)
            currentState.render(gc);
    }

    /**
     * Chuyển tiếp xử lý input tới state hiện tại (nếu có).
     *
     * @param input provider input (I_InputProvider)
     */
    public void handleInput(I_InputProvider input) {
        if (currentState != null)
            currentState.handleInput(input);
    }
}