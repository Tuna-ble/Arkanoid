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

    private double trackOffsetX;
    private double trackOffsetY;
    private double trackWidth;
    private double trackHeight;
    private double handleX;
    private double handleWidth = height / 2.0;
    private double handleHeight = height * 1.4;

    private Rectangle bounds;

    public Slider(double x, double y, double width, double height, double initialValue) {
        super(x, y, width, height);
        setValue(initialValue); // Đặt giá trị ban đầu

        AssetManager am = AssetManager.getInstance();
        this.frameImage = am.getImage("barFrame");
        this.fillImage = am.getImage("barFill");
        this.handleImage = am.getImage("barHandle");

        this.bounds = new Rectangle(x, y, width, height);
        this.trackOffsetX = 1;
        this.trackOffsetY = 1;
        this.trackWidth = width - (2 * trackOffsetX);
        this.trackHeight = height - (2 * trackOffsetX);
    }

    public void setValue(double value) {
        this.value = Math.max(0.0, Math.min(1.0, value));
        // Cập nhật vị trí X của núm dựa trên giá trị
        this.handleX = this.x + (this.width * this.value);
    }

    public double getValue() {
        return this.value;
    }

    public void handleInput(I_InputProvider input) {
        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();
        boolean mousePressed = input.isMousePressed();

        // Tính khoảng cách từ chuột đến tâm núm
        double distToHandle = Math.hypot(mouseX - handleX, mouseY - (y + height / 2));

        // 1. Bắt đầu kéo?
        if (mousePressed && distToHandle <= handleWidth / 2.0 && !isDragging) {
            isDragging = true;
        }

        // 2. Dừng kéo?
        if (!mousePressed) {
            isDragging = false;
        }

        // 3. Đang kéo?
        if (isDragging) {
            // Tìm vị trí X mới, kẹp (clamp) trong khoảng [x, x + width]
            double newHandleX = Math.max(this.x, Math.min(mouseX, this.x + this.width));

            // Cập nhật giá trị
            this.value = (newHandleX - this.x) / this.width;

            // Cập nhật vị trí X của núm
            this.handleX = newHandleX;
        }
    }

    public void renderDefault(GraphicsContext gc) {
        // 1. Vẽ thanh rãnh (track)
        gc.drawImage(frameImage, x, y, width, height);

        // 2. Vẽ phần "đã điền" (filled)
        double sx = 0;
        double sy = 0;
        // Đây là mấu chốt: chỉ lấy một phần chiều rộng của ảnh fill
        double sw = width * value;
        double sh = height;

        // Tọa độ và kích thước đích (destination)
        // (Vẽ nó đè lên frame, có xét khoảng đệm)
        double dx = this.x + trackOffsetX;
        double dy = this.y + trackOffsetY;
        // Chiều rộng đích bằng chiều rộng nguồn
        double dw = sw;
        double dh = sh;

        // Chỉ vẽ nếu có giá trị
        if (value > 0) {
            gc.drawImage(fillImage, sx, sy, sw, sh, dx, dy, dw, dh);
        }

        // 3. Vẽ núm (knob)
        gc.drawImage(handleImage, handleX, y, handleWidth, handleHeight);
    }
}
