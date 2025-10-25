package org.example.gamelogic;

import java.util.Set;

public interface I_InputProvider {
    Set<Integer> getPressedKeys();
    boolean isKeyPressed(int keyCode);
    int getMouseX();
    int getMouseY();
    boolean isMouseClicked();
    void resetMouseClick();
}
