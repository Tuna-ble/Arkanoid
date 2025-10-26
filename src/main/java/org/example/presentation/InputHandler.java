package org.example.presentation;

import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.states.GameState;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class InputHandler implements I_InputProvider {
    private GameState currentState;
    private Set<KeyCode> pressedKeys;
    private int mouseX;
    private int mouseY;
    private boolean mouseClicked;
    private int lastMouseX;
    private long lastMouseMoveTime;
    private static final long MOUSE_ACTIVE_MS = 150;

    public InputHandler(GameState initialState) {
        this.currentState = initialState;
        this.pressedKeys = new HashSet<>();
        this.mouseX = 0;
        this.mouseY = 0;
        this.mouseClicked = false;
        this.lastMouseX = 0;
        this.lastMouseMoveTime = 0;
    }

    public void setCurrentState(GameState newState) {
        this.currentState = newState;
    }

    public void handleInput() {
        if (currentState != null) {
            currentState.handleInput(this);
        }
    }

    // Key
    public void keyPressed(KeyCode keyCode) {
        pressedKeys.add(keyCode);
    }

    public void keyReleased(KeyCode keyCode) {
        pressedKeys.remove(keyCode);
    }

    @Override
    public Set<Integer> getPressedKeys() {
        Set<Integer> keyCodes = new HashSet<>();
        for (KeyCode keyCode : pressedKeys) {
            keyCodes.add(keyCode.getCode());
        }
        return keyCodes;
    }

    public boolean isKeyPressed(KeyCode keyCode) {
        return pressedKeys.contains(keyCode);
    }

    // Mouse.
    public void mouseMoved(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        if (x != lastMouseX) {
            lastMouseX = x;
            lastMouseMoveTime = System.currentTimeMillis();
        }
    }

    public void mousePressed(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        this.mouseClicked = true;
        lastMouseX = x;
        lastMouseMoveTime = System.currentTimeMillis();
    }

    public void resetMouseClick() {
        mouseClicked = false;
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

    // kiểm tra chuột vừa di chuyển trong ngưỡng thời gian
    public boolean isMouseActive() {
        return System.currentTimeMillis() - lastMouseMoveTime <= MOUSE_ACTIVE_MS;
    }

    // helper: nếu muốn biết chuột có trong bounds
    public boolean isMouseInBounds(double width, double height) {
        return mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= height;
    }
}
