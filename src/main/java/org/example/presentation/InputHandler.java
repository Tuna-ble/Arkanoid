package org.example.presentation;

import javafx.scene.input.KeyCode;
import org.example.gamelogic.I_InputProvider;
import java.util.HashSet;
import java.util.Set;

/**
 * Lớp xử lý input cho JavaFX.
 * Lớp này KHÔNG implement listener, nó chỉ lưu trữ trạng thái.
 * Các sự kiện (events) sẽ được đăng ký từ lớp Main (nơi có Scene).
 */
public class InputHandler implements I_InputProvider {

    private Set<KeyCode> pressedKeys;
    private int mouseX;
    private int mouseY;
    private boolean mouseClicked;

    public InputHandler() {
        this.pressedKeys = new HashSet<>();
        this.mouseX = 0;
        this.mouseY = 0;
        this.mouseClicked = false;
    }

    public void addKey(KeyCode code) {
        pressedKeys.add(code);
    }

    public void removeKey(KeyCode code) {
        pressedKeys.remove(code);
    }

    public void setMousePos(double x, double y) {
        this.mouseX = (int) x;
        this.mouseY = (int) y;
    }

    public void setMouseClicked(boolean clicked) {
        this.mouseClicked = clicked;
    }

    @Override
    public Set<KeyCode> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }

    public boolean isKeyPressed(KeyCode code) {
        return pressedKeys.contains(code);
    }

    @Override
    public void clear() {
        pressedKeys.clear();
    }

    @Override
    public int getMouseX() {
        return mouseX;
    }

    @Override
    public int getMouseY() {
        return mouseY;
    }

    @Override
    public boolean isMouseClicked() {
        return mouseClicked;
    }

    public void resetMouseClick() {
        mouseClicked = false;
    }
}