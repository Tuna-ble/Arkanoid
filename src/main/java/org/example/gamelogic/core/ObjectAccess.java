package org.example.gamelogic.core;

import org.example.gamelogic.entities.Paddle;

public final class ObjectAccess {
    private static class SingletonHolder {
        private static final ObjectAccess INSTANCE = new ObjectAccess();
    }

    public static ObjectAccess getInstance() {
        return SingletonHolder.INSTANCE;
    }


    private Paddle paddle;

    private ObjectAccess() {}

    public void registerPaddle(Paddle paddle) {
        if (paddle == null) {
            return;
        }
        this.paddle = paddle;
    }

    public Paddle getPaddle() {
        if (this.paddle == null) {
            throw new RuntimeException("LỖI: Paddle chưa được đăng ký trong GameWorld. " +
                    "Hãy gọi GameWorld.getInstance().registerPaddle(paddle) khi khởi tạo game.");
        }
        return this.paddle;
    }
}
