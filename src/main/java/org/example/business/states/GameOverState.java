package org.example.business.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import jdk.internal.util.xml.impl.Input;
import org.example.presentation.controller.InputHandler;

import javax.swing.*;

public class GameOverState implements GameState {
    private static final int SCREEN_WIDTH = 960;
    private static final int SCREEN_HEIGHT = 600;
    @Override
    public void update() {
        handleInput();
    }

    @Override
    public void render(GraphicsContext gc) {

        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, 960,600);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        gc.setFill(Color.RED);
        gc.setTextAlign(TextAlignment.CENTER);

        gc.fillText("GAME OVER", SCREEN_HEIGHT/ 2.0,SCREEN_HEIGHT/ 2.0);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.setFill(Color.GREEN);
        gc.fillText("FINAL SCORE",SCREEN_HEIGHT/ 2.0,SCREEN_HEIGHT/ 2.0  );

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.fillText("Press Enter to return to main menu",SCREEN_HEIGHT/ 2.0,SCREEN_HEIGHT/ 2.0  );

    }

    @Override
    public void handleInput() {
        if(InputHandler.isKeyPressed(KeyCode.ENTER)) {
            JCheckBoxMenuItem stateManager = null;
            stateManager.setState(Boolean.parseBoolean("MENU"));
        }
    }
}
