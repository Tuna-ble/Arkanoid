package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;

public class Slider extends AbstractUIElement {
    private double value;
    private boolean isDragging = false;

    private Image frameImage;
    private Image fillImage;
    private Image handleImage;

    private double trackX;
    private double trackY;
    private double trackWidth;
    private double trackHeight;
    private double handleX;
    private double handleWidth = height / 2.0;
    private double handleHeight = height * 1.4;

    private Rectangle bounds;

    public Slider(double x, double y, double width, double height, double initialValue) {
        super(x, y, width, height);

        AssetManager am = AssetManager.getInstance();
        this.frameImage = am.getImage("barFrame");
        this.fillImage = am.getImage("barFill");
        this.handleImage = am.getImage("barHandle");

        this.bounds = new Rectangle(x, y, width, height);
        double PADDING = 2;
        this.trackX = this.x + PADDING;
        this.trackY = this.y + PADDING;
        this.trackWidth = this.width - (2 * PADDING);
        this.trackHeight = this.height - (2 * PADDING);

        setValue(initialValue);
    }

    public void setValue(double value) {
        this.value = Math.max(0.0, Math.min(1.0, value));

        this.handleX = this.trackX + (this.trackWidth * this.value);
    }

    public double getValue() {
        return this.value;
    }

    public void handleInput(I_InputProvider input) {
        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();
        boolean mousePressed = input.isMousePressed();

        if (mousePressed) {
            if (!isDragging && bounds.contains(mouseX, mouseY)) {
                isDragging = true;
            }

            if (isDragging) {
                double newHandleX = Math.max(this.trackX, Math.min(mouseX, this.trackX + this.trackWidth));

                this.value = (newHandleX - this.trackX) / this.trackWidth;
                this.handleX = newHandleX;
            }
        } else {
            isDragging = false;
        }
    }

    public void renderDefault(GraphicsContext gc) {
        gc.drawImage(frameImage, x, y, width, height);

        if (value > 0 && fillImage != null) {
            double dw = this.trackWidth * value;
            double dh = this.trackHeight;

            double sw = this.fillImage.getWidth() * value;
            double sh = this.fillImage.getHeight();

            gc.drawImage(
                    fillImage,
                    0, 0, sw, sh,
                    trackX, trackY, dw, dh
            );
        }

        if (handleImage != null) {
            double drawHandleX = this.handleX - (this.handleWidth / 2.0);
            double drawHandleY = this.y + (this.height / 2.0) - (this.handleHeight / 2.0);

            gc.drawImage(handleImage, drawHandleX, drawHandleY, handleWidth, handleHeight);
        }
    }
}
