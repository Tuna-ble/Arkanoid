package org.example.gamelogic.events;

/**
 * Lớp sự kiện (Event) được phát (publish)
 * khi người chơi bị mất một mạng.
 * <p>
 * Lớp này mang thông tin về số mạng còn lại
 * ({@code remainingLives})
 * để các hệ thống khác (như {@code PlayingState})
 * có thể cập nhật.
 */
public final class LifeLostEvent extends GameEvent {
    private final int remainingLives;

    /**
     * Khởi tạo sự kiện LifeLost.
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ số mạng còn lại
     * sau khi người chơi bị mất mạng.
     * <p>
     * <b>Expected:</b> Một đối tượng sự kiện được tạo,
     * chứa thông tin về số mạng còn lại.
     *
     * @param remainingLives Số mạng còn lại (sau khi đã trừ).
     */
    public LifeLostEvent(int remainingLives) {
        this.remainingLives = remainingLives;
    }

    /**
     * Lấy về số mạng còn lại.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị
     * {@code remainingLives} đã được lưu.
     * <p>
     * <b>Expected:</b> Trả về số mạng (int)
     * mà người chơi còn lại.
     *
     * @return Số mạng còn lại.
     */
    public int getRemainingLives() {
        return remainingLives;
    }
}