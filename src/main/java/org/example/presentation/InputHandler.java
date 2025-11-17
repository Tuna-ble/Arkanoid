package org.example.presentation;

import javafx.scene.input.KeyCode;
import org.example.gamelogic.I_InputProvider;
import java.util.HashSet;
import java.util.Set;

/**
 * Lớp xử lý input cho JavaFX.
 * Lớp này KHÔNG implement listener, nó chỉ lưu trữ trạng thái.
 * Các events sẽ được đăng ký từ lớp có {@link javafx.scene.Scene}.
 */
public class InputHandler implements I_InputProvider {

    private Set<KeyCode> pressedKeys;
    private int mouseX;
    private int mouseY;
    private boolean mouseClicked;
    private boolean mouseIsPressed;

    /**
     * Khởi tạo bộ xử lý input, thiết lập trạng thái ban đầu cho bàn phím và chuột.
     */
    public InputHandler() {
        this.pressedKeys = new HashSet<>();
        this.mouseX = 0;
        this.mouseY = 0;
        this.mouseClicked = false;
        this.mouseIsPressed = false;
    }

    /**
     * Thêm một phím vào danh sách đang được nhấn.
     *
     * @param code mã phím được nhấn
     */
    public void addKey(KeyCode code) {
        pressedKeys.add(code);
    }

    /**
     * Xóa một phím khỏi danh sách đang nhấn.
     *
     * @param code mã phím được thả ra
     */
    public void removeKey(KeyCode code) {
        pressedKeys.remove(code);
    }

    /**
     * Cập nhật vị trí chuột hiện tại.
     *
     * @param x tọa độ X của chuột
     * @param y tọa độ Y của chuột
     */
    public void setMousePos(double x, double y) {
        this.mouseX = (int) x;
        this.mouseY = (int) y;
    }

    /**
     * Đánh dấu trạng thái chuột vừa click (one-shot).
     *
     * @param clicked true nếu chuột vừa click
     */
    public void setMouseClicked(boolean clicked) {
        this.mouseClicked = clicked;
    }

    /**
     * Cập nhật trạng thái chuột đang được giữ.
     *
     * @param pressed true nếu chuột đang được nhấn giữ
     */
    public void setMousePressed(boolean pressed) {
        this.mouseIsPressed = pressed;
    }

    /**
     * Đặt trạng thái chuột về "không giữ".
     * Thường gọi khi nhận mouse released event.
     */
    public void setMouseReleased() {
        this.mouseIsPressed = false;
    }

    /**
     * Trả về tập các phím đang được nhấn (bản sao).
     *
     * @return Set mới chứa các {@link KeyCode} đang active
     */
    @Override
    public Set<KeyCode> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }

    /**
     * Kiểm tra một phím có đang được nhấn hay không.
     *
     * @param code mã phím cần kiểm tra
     * @return true nếu phím đang được nhấn
     */
    public boolean isKeyPressed(KeyCode code) {
        return pressedKeys.contains(code);
    }

    /**
     * Xóa toàn bộ trạng thái bàn phím (thường dùng khi reset state).
     */
    @Override
    public void clear() {
        pressedKeys.clear();
    }

    /**
     * @return tọa độ X hiện tại của chuột
     */
    @Override
    public int getMouseX() {
        return mouseX;
    }

    /**
     * @return tọa độ Y hiện tại của chuột
     */
    @Override
    public int getMouseY() {
        return mouseY;
    }

    /**
     * @return true nếu chuột vừa click (one-shot, sẽ reset sau khi xử lý)
     */
    @Override
    public boolean isMouseClicked() {
        return mouseClicked;
    }

    /**
     * @return true nếu chuột đang được giữ (hold)
     */
    @Override
    public boolean isMousePressed() {
        return mouseIsPressed;
    }

    /**
     * Reset trạng thái mouse click sau khi game đã xử lý click.
     */
    @Override
    public void resetMouseClick() {
        mouseClicked = false;
    }
}
