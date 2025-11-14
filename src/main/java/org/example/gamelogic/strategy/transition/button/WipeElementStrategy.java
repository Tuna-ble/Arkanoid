package org.example.gamelogic.strategy.transition.button;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractUIElement;

public class WipeElementStrategy implements IUIElementTransitionStrategy {
    private double duration;
    private double timer;
    private boolean started;
    private boolean finished;

    public WipeElementStrategy(double duration) {
        this.duration = (duration <= 0) ? 0.1 : duration;
        this.timer = 0;
        this.started = false;
        this.finished = false;
    }

    @Override public void start() { this.started = true; }
    @Override public boolean isFinished() { return finished; }

    @Override
    public void update(double deltaTime) {
        if (!started || finished) return;
        timer += deltaTime;
        if (timer >= duration) {
            timer = duration;
            finished = true;
        }
    }

    @Override
    public void renderElement(GraphicsContext gc, AbstractUIElement element) {
        double progress = timer / duration;
        if (progress <= 0.0) return;

        double currentWidth = element.getWidth() * progress;

        gc.save();

        try {
            // 2. Tạo một hình chữ nhật "clip" (giống như tấm che)
            gc.beginPath();
            gc.rect(
                    element.getX(),
                    element.getY(),
                    currentWidth,
                    element.getHeight()
            );
            gc.clip(); // Chỉ vẽ bất cứ thứ gì BÊN TRONG hình chữ nhật này

            // 3. Bảo element tự vẽ 100%
            // Button sẽ tự dùng image, text CỦA NÓ
            element.renderDefault(gc);

        } finally {
            // 4. Xóa tấm che, trả canvas về bình thường
            gc.restore();
        }
    }
}
