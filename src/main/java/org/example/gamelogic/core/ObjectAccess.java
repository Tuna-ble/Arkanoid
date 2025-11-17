package org.example.gamelogic.core;

import org.example.gamelogic.entities.Paddle;

/**
 * Cung cấp truy cập tới các đối tượng toàn cục của game (ví dụ paddle).
 *
 * <p>Singleton đơn giản dùng để đăng ký và lấy paddle từ các thành phần không dễ truyền dependency.
 */
public final class ObjectAccess {
    private static class SingletonHolder {
        private static final ObjectAccess INSTANCE = new ObjectAccess();
    }

    /**
     * Lấy instance đơn của ObjectAccess.
     *
     * @return singleton ObjectAccess
     */
    public static ObjectAccess getInstance() {
        return SingletonHolder.INSTANCE;
    }


    private Paddle paddle;

    private ObjectAccess() {}

    /**
     * Đăng ký paddle toàn cục để các thành phần khác có thể truy xuất.
     *
     * @param paddle đối tượng Paddle (nếu null sẽ bỏ qua)
     */
    public void registerPaddle(Paddle paddle) {
        if (paddle == null) {
            return;
        }
        this.paddle = paddle;
    }

    /**
     * Lấy paddle đã đăng ký.
     *
     * @return Paddle đã đăng ký
     * @throws RuntimeException nếu paddle chưa được đăng ký
     */
    public Paddle getPaddle() {
        if (this.paddle == null) {
            throw new RuntimeException("LỖI: Paddle chưa được đăng ký trong GameWorld. " +
                    "Hãy gọi GameWorld.getInstance().registerPaddle(paddle) khi khởi tạo game.");
        }
        return this.paddle;
    }
}
