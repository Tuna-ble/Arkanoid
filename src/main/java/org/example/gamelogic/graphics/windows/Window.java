package org.example.gamelogic.graphics.windows;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.graphics.TextRenderer;
import org.example.gamelogic.graphics.buttons.AbstractUIElement;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.gamelogic.states.GameState;
import org.example.gamelogic.strategy.transition.button.WipeElementTransitionStrategy;
import org.example.gamelogic.strategy.transition.window.ITransitionStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Quản lý một cửa sổ (Window) giao diện người dùng (UI) chung.
 * <p>
 * Lớp này hoạt động như một container cho các {@link AbstractUIElement},
 * hiển thị (render) trạng thái trước đó ({@code previousState}) bên dưới
 * với một lớp phủ mờ. Nó quản lý hiệu ứng chuyển cảnh (transition)
 * của chính nó và của các
 * thành phần con (elements) chứa bên trong.
 */
public class Window {
    private double x, y, width, height;;

    protected List<AbstractUIElement> elements = new ArrayList<>();
    protected GameState previousState;
    private ITransitionStrategy windowTransition;

    private boolean windowTransitionFinished = false;
    private boolean childrenTransitionsStarted = false;

    /**
     * Khởi tạo một cửa sổ (Window) mới.
     * <p>
     * <b>Định nghĩa:</b> Tính toán vị trí (x, y) để căn giữa cửa sổ
     * dựa trên {@code windowWidth} và {@code windowHeight}.
     * Lưu trữ {@code previousState} (để render nền)
     * và {@code windowTransition} (để chạy hiệu ứng).
     * <p>
     * <b>Expected:</b> Đối tượng Window được tạo,
     * định vị ở giữa màn hình,
     * và sẵn sàng để bắt đầu hiệu ứng transition khi {@code update} được gọi.
     *
     * @param previousState    Trạng thái game (GameState)
     * sẽ được render bên dưới (nền mờ).
     * @param windowWidth      Chiều rộng của cửa sổ.
     * @param windowHeight     Chiều cao của cửa sổ.
     * @param windowTransition Chiến lược (Strategy)
     * hiệu ứng chuyển cảnh cho cửa sổ này.
     */
    public Window(GameState previousState, double windowWidth, double windowHeight,
                  ITransitionStrategy windowTransition) {
        this.previousState = previousState;

        this.width = windowWidth;
        this.height = windowHeight;
        this.x = GameConstants.SCREEN_WIDTH / 2.0 - windowWidth / 2.0;
        this.y = GameConstants.SCREEN_HEIGHT / 2.0 - windowHeight / 2.0;
        this.windowTransition = windowTransition;
        if (this.windowTransition == null) {
            this.windowTransitionFinished = true;
        }
    }

    /**
     * Cập nhật logic của cửa sổ và các thành phần con.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code windowTransition} (hiệu ứng của cửa sổ).
     * Khi hiệu ứng cửa sổ hoàn tất, bắt đầu (lần đầu)
     * và cập nhật hiệu ứng của tất cả các thành phần con (elements).
     * <p>
     * <b>Expected:</b> Hiệu ứng của cửa sổ và các nút bên trong
     * được cập nhật theo {@code deltaTime}.
     * {@code windowTransitionFinished} và
     * {@code childrenTransitionsStarted}
     * được cập nhật.
     *
     * @param deltaTime Thời gian (giây) kể từ frame trước.
     */
    public void update(double deltaTime) {
        if (!windowTransitionFinished) {
            windowTransition.update(deltaTime);
            if (windowTransition.isFinished()) {
                windowTransitionFinished = true;
            }
        }

        if (windowTransitionFinished) {

            if (!childrenTransitionsStarted) {
                for (AbstractUIElement element : elements) {
                    element.startTransition();
                }
                childrenTransitionsStarted = true;
            }

            for (AbstractUIElement element : elements) {
                element.updateTransition(deltaTime);
            }
        }
    }

    /**
     * Vẽ (render) cửa sổ và các thành phần của nó.
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code previousState} (nếu có),
     * sau đó vẽ một lớp phủ mờ (dim overlay).
     * Gọi {@code windowTransition.render()} để vẽ cửa sổ
     * (có hiệu ứng). Nếu hiệu ứng cửa sổ xong,
     * vẽ tất cả các thành phần con.
     * <p>
     * <b>Expected:</b> Cửa sổ được vẽ lên {@code gc}
     * (bao gồm nền mờ, hiệu ứng, và các nút).
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    public void render(GraphicsContext gc) {
        if (previousState != null) {
            previousState.render(gc);
            gc.save();
            try {
                gc.setFill(new Color(0, 0, 0, 0.6));
                gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
            } finally {
                gc.restore();
            }

        }

        if (windowTransition != null) {
            windowTransition.render(gc, this);
        }
        if (windowTransitionFinished) {
            for (AbstractUIElement element : elements) {
                element.render(gc);
            }
        }
    }

    /**
     * (Helper) Chỉ vẽ nội dung (các thành phần con) của cửa sổ.
     * <p>
     * <b>Định nghĩa:</b> Lặp và render tất cả
     * {@code elements} (nút, thanh trượt,...).
     * Phương thức này thường được gọi bởi
     * một {@link ITransitionStrategy}.
     * <p>
     * <b>Expected:</b> Tất cả các thành phần con được vẽ,
     * không bao gồm nền mờ hay hiệu ứng cửa sổ.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    public void renderContents(GraphicsContext gc) {
        for (AbstractUIElement element : elements) {
            element.render(gc);
        }
    }

    /**
     * Xử lý input (click chuột, hover) cho các thành phần con.
     * <p>
     * <b>Định nghĩa:</b> Chỉ xử lý input nếu cả hiệu ứng
     * của cửa sổ và tất cả các thành phần con đã hoàn tất.
     * Ủy quyền (delegate) xử lý input cho từng {@code element}.
     * <p>
     * <b>Expected:</b> Các nút (buttons)
     * và các element khác nhận và xử lý input.
     * Input bị chặn (ignored) nếu transition chưa xong.
     *
     * @param input Nguồn cung cấp input (phím, chuột).
     */
    public void handleInput(I_InputProvider input) {
        boolean allChildrenFinished = true;
        for (AbstractUIElement element : elements) {
            if (!element.isTransitionFinished()) {
                allChildrenFinished = false;
                break;
            }
        }

        if (windowTransitionFinished && allChildrenFinished) {
            for (AbstractUIElement element : elements) {
                element.handleInput(input);
            }
        }
    }

    /**
     * Thêm một thành phần UI (nút, nhãn,...) vào cửa sổ.
     * <p>
     * <b>Định nghĩa:</b> Thêm {@code element} vào danh sách {@code elements}.
     * <p>
     * <b>Expected:</b> {@code element} được thêm vào danh sách
     * và sẽ được update/render bởi Window.
     *
     * @param element Thành phần UI cần thêm.
     */
    public void addButton(AbstractUIElement element) {
        this.elements.add(element);
    }

    /**
     * Lấy chiều rộng của cửa sổ.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code width}.
     * <p>
     * <b>Expected:</b> Giá trị (double) của chiều rộng.
     *
     * @return Chiều rộng.
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Lấy chiều cao của cửa sổ.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code height}.
     * <p>
     * <b>Expected:</b> Giá trị (double) của chiều cao.
     *
     * @return Chiều cao.
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Lấy tọa độ X (bên trái) của cửa sổ.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code x}.
     * <p>
     * <b>Expected:</b> Giá trị (double) của tọa độ X.
     *
     * @return Tọa độ X.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Lấy tọa độ Y (bên trên) của cửa sổ.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code y}.
     * <p>
     * <b>Expected:</b> Giá trị (double) của tọa độ Y.
     *
     * @return Tọa độ Y.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Kiểm tra xem hiệu ứng transition CỦA CỬA SỔ đã hoàn tất chưa.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của
     * {@code windowTransitionFinished}.
     * <p>
     * <b>Expected:</b> {@code true} nếu hiệu ứng
     * của window đã xong, ngược lại {@code false}.
     *
     * @return Trạng thái hoàn tất của transition cửa sổ.
     */
    public boolean transitionFinished() {
        return this.windowTransitionFinished;
    }

    /**
     * Lấy danh sách tất cả các thành phần con (elements).
     * <p>
     * <b>Định nghĩa:</b> Trả về tham chiếu đến danh sách
     * {@code elements}.
     * <p>
     * <b>Expected:</b> {@code List<AbstractUIElement>}
     * chứa các nút, nhãn,...
     *
     * @return Danh sách các thành phần UI.
     */
    public List<AbstractUIElement> getElements() {
        return this.elements;
    }
}