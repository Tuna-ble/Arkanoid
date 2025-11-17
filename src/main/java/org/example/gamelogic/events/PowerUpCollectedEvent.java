package org.example.gamelogic.events;

import org.example.gamelogic.entities.powerups.PowerUp;

/**
 * Lớp sự kiện (Event) được phát (publish)
 * khi người chơi thu thập (nhặt) một PowerUp.
 * <p>
 * Lớp này mang thông tin về {@link PowerUp}
 * đã được thu thập để các hệ thống khác
 * (như {@code PlayingState}) có thể xử lý.
 */
public final class PowerUpCollectedEvent extends GameEvent {
    private final PowerUp collectedPowerUp;

    /**
     * Khởi tạo sự kiện PowerUpCollected.
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ tham chiếu
     * đến đối tượng {@link PowerUp}
     * vừa được thu thập.
     * <p>
     * <b>Expected:</b> Một đối tượng sự kiện được tạo,
     * chứa thông tin về {@code powerUp}.
     *
     * @param powerUp Đối tượng PowerUp vừa được thu thập.
     */
    public PowerUpCollectedEvent(PowerUp powerUp) {
        this.collectedPowerUp = powerUp;
    }

    /**
     * Lấy về đối tượng PowerUp đã được thu thập.
     * <p>
     * <b>Định nghĩa:</b> Trả về tham chiếu
     * đến {@code collectedPowerUp}.
     * <p>
     * <b>Expected:</b> Trả về {@link PowerUp}
     * mà sự kiện này mang theo.
     *
     * @return Đối tượng PowerUp đã thu thập.
     */
    public PowerUp getPowerUpCollected() {
        return collectedPowerUp;
    }
}