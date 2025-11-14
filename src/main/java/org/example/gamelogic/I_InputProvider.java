package org.example.gamelogic;

import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public interface I_InputProvider {
    Set<KeyCode> getPressedKeys();
    boolean isKeyPressed(KeyCode code);
    int getMouseX();
    int getMouseY();
    boolean isMouseClicked();
    boolean isMousePressed();
    void resetMouseClick();
    void clear();
}