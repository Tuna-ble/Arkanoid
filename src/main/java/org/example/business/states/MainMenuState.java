package org.example.business.states;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

import static java.awt.Color.*;
import static javax.print.attribute.standard.Chromaticity.COLOR;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import jdk.internal.util.xml.impl.Input;
import org.example.presentation.controller.InputHandler;

class MainMenuState implements GameState {
    private final String[] options = {
            "Start",
            "Exit"
    };
    private Object stateManager = null;

    private int currentOption = 0;
    private static final int SCREEN_WIDTH = 960;
    private static final int SCREEN_HEIGHT = 600;

    public MainMenuState() {
        this.stateManager = stateManager;
    }

    @Override
    public void update() {
        handleInput();
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        gc.setFont(Font.font("Impact", FontWeight.BOLD, 90));
        gc.setFill(Color.web("#f7ca18"));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("ARKANOID",100,100);

        gc.setFont(Font.font("Impact", FontWeight.BOLD, 90));
        for(int i = 0; i < options.length; i++) {
            if(i == currentOption) {
                gc.setFill(Color.YELLOW);
            }
            else {
                gc.setFill(Color.WHITE);
            }
            gc.fillText(options[i],SCREEN_HEIGHT/ 2.0,SCREEN_HEIGHT/ 2.0);
        }
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 90));
        gc.setFill(Color.RED);
        gc.fillText("tutorials",SCREEN_HEIGHT/ 2.0,SCREEN_HEIGHT/ 2.0);
    }

    @Override
    public void handleInput() {
        if(InputHandler.isKeyJustPressed(KeyCode.DOWN)) {
            currentOption++;
            if(currentOption > options.length) {
                currentOption = 0;
            }
        }
        if(InputHandler.isKeyJustPressed(KeyCode.UP)) {
            currentOption--;
            if(currentOption < 0) {
                currentOption = options.length - 1;
            }
        }
        if(InputHandler.isKeyJustPressed(KeyCode.ENTER)) {
            selectOption();
        }
    }

    private void selectOption() {
        switch(currentOption) {
            case 0:
                stateManager.getClass();
                break;
            case 1:
                System.exit(0);
                break;
        }
    }
}
