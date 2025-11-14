package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;

public class ToggleButton extends AbstractButton {
    private Image onImage;
    private Image offImage;
    private boolean isOn = false;

    public ToggleButton(double x, double y, double width, double height,
                        Image onImage, Image offImage, boolean initialState) {
        super(x, y, width, height, null);
        this.onImage = onImage;
        this.offImage = offImage;
        this.isOn = initialState;
    }

    public ToggleButton(double x, double y, Image onImage, Image offImage, boolean initialState) {
        this(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT,
                onImage, offImage, initialState);
    }

    @Override
    public void handleInput(I_InputProvider inputProvider) {
        super.handleInput(inputProvider);

        if (isClicked) {
            this.isOn = !this.isOn;
        }
    }

    @Override
    public void renderDefault(GraphicsContext gc) {
        Image imgToDraw = isOn ? onImage : offImage;

        if (imgToDraw != null) {
            gc.drawImage(imgToDraw, x, y, width, height);
        }
    }

    public boolean isOn() {
        return this.isOn;
    }
}