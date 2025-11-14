package org.example.gamelogic.graphics.windows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractUIElement;
import org.example.gamelogic.states.GameState;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;

import java.util.ArrayList;
import java.util.List;

public class Window {
    private double x, y, width, height;
    protected String title;
    protected Font titleFont;
    protected Color titleColor;

    protected List<AbstractUIElement> elements = new ArrayList<>();
    protected GameState previousState;
    private ITransitionStrategy windowTransition;

    private boolean windowTransitionFinished = false;
    private boolean childrenTransitionsStarted = false;

    public Window(GameState previousState, double windowWidth, double windowHeight,
                  ITransitionStrategy windowTransition, String title, Font titleFont) {
        this.previousState = previousState;

        this.width = windowWidth;
        this.height = windowHeight;
        this.x = GameConstants.SCREEN_WIDTH / 2.0 - windowWidth / 2.0;
        this.y = GameConstants.SCREEN_HEIGHT / 2.0 - windowHeight / 2.0;
        this.title = title;
        this.titleFont = (titleFont != null) ? titleFont : new Font("Arial", 48);
        this.titleColor = Color.WHITE;

        this.windowTransition = windowTransition;
        if (this.windowTransition == null) {
            this.windowTransitionFinished = true; // Không có -> xong luôn
        }
    }

    public void update(double deltaTime) {
        if (!windowTransitionFinished) {
            windowTransition.update(deltaTime);
            if (windowTransition.isFinished()) {
                windowTransitionFinished = true;
            }
        }

        // 2. "SAU KHI" nó xong...
        if (windowTransitionFinished) {

            // 2a. Ra lệnh cho TẤT CẢ con bắt đầu (chỉ 1 lần)
            if (!childrenTransitionsStarted) {
                for (AbstractUIElement element : elements) {
                    element.startTransition(); // Bảo chúng nó "Chạy đi!"
                }
                childrenTransitionsStarted = true;
            }

            // 2b. Update transition CỦA CON
            for (AbstractUIElement element : elements) {
                element.updateTransition(deltaTime); // "Tiếp tục chạy đi!"
            }
        }
    }

    public void render(GraphicsContext gc) {
        if (previousState != null) {
            previousState.render(gc);
        }
        gc.setFill(new Color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        if (windowTransition != null) {
            windowTransition.render(gc, this);
        }
        if (windowTransitionFinished) {
            for (AbstractUIElement element : elements) {
                element.render(gc); // Element tự biết vẽ (strategy hay default)
            }
        }
    }

    public void renderContents(GraphicsContext gc) {
        for (AbstractUIElement element : elements) {
            if (title != null) {
                gc.setTextAlign(TextAlignment.CENTER);
                TextRenderer.drawOutlinedText(
                        gc,
                        title,
                        x + width / 2,
                        y + 60,
                        titleFont,
                        titleColor,
                        Color.BLACK, 2.0, null
                );
            }
            element.render(gc);
        }
    }

    public void handleInput(I_InputProvider input) {
        boolean allChildrenFinished = true;
        for (AbstractUIElement element : elements) {
            if (!element.isTransitionFinished()) {
                allChildrenFinished = false;
                break;
            }
        }

        // CHỈ xử lý input KHI CẢ Window VÀ TẤT CẢ con đã xong
        if (windowTransitionFinished && allChildrenFinished) {
            for (AbstractUIElement element : elements) {
                element.handleInput(input); // Gọi logic check hover/click
            }
        }
    }

    public void addButton(AbstractUIElement element) {
        this.elements.add(element);
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public List<AbstractUIElement> getElements() {
        return this.elements;
    }
}
