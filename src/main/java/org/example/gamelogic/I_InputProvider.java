package org.example.gamelogic;

import javafx.scene.input.KeyCode;

import java.util.Set;

public interface I_InputProvider {
    Set<Integer> getPressedKeys();
    boolean isKeyPressed(KeyCode keyCode);
    int getMouseX();
    int getMouseY();
    boolean isMouseClicked();
    void resetMouseClick();
}
