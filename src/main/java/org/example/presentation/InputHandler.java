package org.example.presentation;

import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.states.GameState;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, I_InputProvider {
    private GameState currentState;
    private Set<Integer> pressedKeys;
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

    /**
     * getter, setter
     */
    @Override
    public Set<Integer> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }

    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
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

    /**
     * Key.
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Mouse.
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseClicked = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
