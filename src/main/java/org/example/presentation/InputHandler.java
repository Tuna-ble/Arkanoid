package org.example.presentation;

import javafx.scene.input.KeyCode;
import org.example.gamelogic.I_InputProvider;
import java.util.HashSet;
import java.util.Set;

/**
 * Lớp xử lý input cho JavaFX, implement {@link I_InputProvider}.
 * <p>
 * Lớp này lưu trữ trạng thái phím và chuột. Các sự kiện (events)
 * được đăng ký từ lớp {@link GameApplication} (nơi có Scene).
 */
public class InputHandler implements I_InputProvider {

    private Set<KeyCode> pressedKeys;
    private int mouseX;
    private int mouseY;
    private boolean mouseClicked;
    private boolean mouseIsPressed;

    /**
     * Khởi tạo InputHandler.
     * <p>
     * <b>Định nghĩa:</b> Tạo mới các biến lưu trữ trạng thái input.
     * <p>
     * <b>Expected:</b> Các trạng thái phím/chuột được đặt về giá trị mặc định (rỗng hoặc false).
     */
    public InputHandler() {
        this.pressedKeys = new HashSet<>();
        this.mouseX = 0;
        this.mouseY = 0;
        this.mouseClicked = false;
        this.mouseIsPressed = false;
    }

    /**
     * Ghi nhận một phím đang được nhấn.
     * <p>
     * <b>Định nghĩa:</b> Thêm KeyCode vào danh sách phím đang nhấn.
     * <p>
     * <b>Expected:</b> Phím `code` được thêm vào `pressedKeys`.
     *
     * @param code Phím được nhấn (KeyCode).
     */
    public void addKey(KeyCode code) {
        pressedKeys.add(code);
    }

    /**
     * Ghi nhận một phím vừa được thả.
     * <p>
     * <b>Định nghĩa:</b> Xóa KeyCode khỏi danh sách phím đang nhấn.
     * <p>
     * <b>Expected:</b> Phím `code` được xóa khỏi `pressedKeys`.
     *
     * @param code Phím được thả (KeyCode).
     */
    public void removeKey(KeyCode code) {
        pressedKeys.remove(code);
    }

    /**
     * Cập nhật vị trí (tọa độ) của chuột.
     * <p>
     * <b>Định nghĩa:</b> Lưu trữ tọa độ X và Y của chuột.
     * <p>
     * <b>Expected:</b> `mouseX` và `mouseY` được cập nhật.
     *
     * @param x Tọa độ X mới.
     * @param y Tọa độ Y mới.
     */
    public void setMousePos(double x, double y) {
        this.mouseX = (int) x;
        this.mouseY = (int) y;
    }

    /**
     * Đặt trạng thái "đã click chuột" (thường là true).
     * <p>
     * <b>Định nghĩa:</b> Cập nhật biến trạng thái `mouseClicked`.
     * <p>
     * <b>Expected:</b> `mouseClicked` được đặt thành giá trị `clicked`.
     *
     * @param clicked Trạng thái click mới.
     */
    public void setMouseClicked(boolean clicked) {
        this.mouseClicked = clicked;
    }

    /**
     * Đặt trạng thái "đang nhấn giữ chuột" (thường là true).
     * <p>
     * <b>Định nghĩa:</b> Cập nhật biến trạng thái `mouseIsPressed`.
     * <p>
     * <b>Expected:</b> `mouseIsPressed` được đặt thành giá trị `pressed`.
     *
     * @param pressed Trạng thái nhấn giữ mới.
     */
    public void setMousePressed(boolean pressed) {
        this.mouseIsPressed = pressed;
    }

    /**
     * Ghi nhận sự kiện thả chuột.
     * <p>
     * <b>Định nghĩa:</b> Đặt trạng thái "đang nhấn giữ chuột" về false.
     * <p>
     * <b>Expected:</b> `mouseIsPressed` được đặt thành `false`.
     */
    public void setMouseReleased() {
        this.mouseIsPressed = false;
    }

    /**
     * Lấy danh sách các phím đang được nhấn.
     * <p>
     * <b>Định nghĩa:</b> Trả về một bản sao của Set chứa các phím đang nhấn.
     * <p>
     * <b>Expected:</b> Một `Set<KeyCode>` mới chứa các phím đang nhấn.
     *
     * @return Bản sao của Set các phím đang nhấn.
     */
    @Override
    public Set<KeyCode> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }

    /**
     * Kiểm tra một phím cụ thể có đang được nhấn không.
     * <p>
     * <b>Định nghĩa:</b> Kiểm tra sự tồn tại của `code` trong `pressedKeys`.
     * <p>
     * <b>Expected:</b> Trả về `true` nếu phím đang được nhấn, ngược lại `false`.
     *
     * @param code Phím cần kiểm tra.
     * @return boolean Trạng thái phím.
     */
    public boolean isKeyPressed(KeyCode code) {
        return pressedKeys.contains(code);
    }

    /**
     * Xóa tất cả các phím đang được nhấn (thường dùng khi chuyển State).
     * <p>
     * <b>Định nghĩa:</b> Xóa sạch (clear) `pressedKeys`.
     * <p>
     * <b>Expected:</b> `pressedKeys` trở nên rỗng.
     */
    @Override
    public void clear() {
        pressedKeys.clear();
    }

    /**
     * Lấy tọa độ X của chuột.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị `mouseX` đã lưu.
     * <p>
     * <b>Expected:</b> Tọa độ X (int) của chuột.
     *
     * @return Tọa độ X.
     */
    @Override
    public int getMouseX() {
        return mouseX;
    }

    /**
     * Lấy tọa độ Y của chuột.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị `mouseY` đã lưu.
     * <p>
     * <b>Expected:</b> Tọa độ Y (int) của chuột.
     *
     * @return Tọa độ Y.
     */
    @Override
    public int getMouseY() {
        return mouseY;
    }

    /**
     * Kiểm tra nếu một cú click chuột vừa xảy ra.
     * <p>
     * <b>Định nghĩa:</b> Trả về trạng thái `mouseClicked`.
     * <p>
     * <b>Expected:</b> `true` nếu có click, `false` nếu không.
     *
     * @return boolean Trạng thái click.
     */
    @Override
    public boolean isMouseClicked() {
        return mouseClicked;
    }

    /**
     * Kiểm tra nếu chuột đang được nhấn giữ.
     * <p>
     * <b>Định nghĩa:</b> Trả về trạng thái `mouseIsPressed`.
     * <p>
     * <b>Expected:</b> `true` nếu đang nhấn giữ, `false` nếu không.
     *
     * @return boolean Trạng thái nhấn giữ.
     */
    @Override
    public boolean isMousePressed() {
        return mouseIsPressed;
    }

    /**
     * Đặt lại trạng thái click (sau khi đã xử lý xong).
     * <p>
     * <b>Định nghĩa:</b> Đặt `mouseClicked` về `false`.
     * <p>
     * <b>Expected:</b> `mouseClicked` là `false` để tránh xử lý click nhiều lần.
     */
    @Override
    public void resetMouseClick() {
        mouseClicked = false;
    }
}