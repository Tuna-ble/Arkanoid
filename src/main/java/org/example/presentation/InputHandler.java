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

    public InputHandler(GameState initialState) {
        this.currentState = initialState;
        this.pressedKeys = new HashSet<>();
        this.mouseX = 0;
        this.mouseY = 0;
        this.mouseClicked = false;
    }

    public void setCurrentState(GameState newState) {
        this.currentState = newState;
    }

    public void handleInput() {
        if (currentState != null) {
            currentState.handleInput(this);
        }
    }

    public void keyPressed(KeyCode keyCode) {
        pressedKeys.add(keyCode);
    }

    public void keyReleased(KeyCode keyCode) {
        pressedKeys.remove(keyCode);
    }

    public void mouseMoved(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    public void mousePressed(int x, int y) {
        this.mouseX = x;
        this.mouseY = y;
        this.mouseClicked = true;
    }

    public void resetMouseClick() {
        mouseClicked = false;
    }

    /**
     * Getter methods for I_InputProvider interface
     */
    @Override
    public Set<Integer> getPressedKeys() {
        Set<Integer> keyCodes = new HashSet<>();
        for (KeyCode keyCode : pressedKeys) {
            keyCodes.add(keyCode.getCode());
        }
        return keyCodes;
    }

    public boolean isKeyPressed(int keyCode) {
        for (KeyCode kc : pressedKeys) {
            if (kc.getCode() == keyCode) {
                return true;
            }
        }
        return false;
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
}
