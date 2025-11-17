package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;

/**
 * Quản lý một nút bấm có hai trạng thái (bật/tắt - on/off).
 * <p>
 * Lớp này kế thừa {@link AbstractButton} và thay đổi hình ảnh
 * ({@code onImage}, {@code offImage})
 * dựa trên trạng thái {@code isOn} của nó.
 */
public class ToggleButton extends AbstractButton {
    private Image onImage;
    private Image offImage;
    private boolean isOn = false;

    /**
     * Khởi tạo ToggleButton với kích thước tùy chỉnh.
     * <p>
     * <b>Định nghĩa:</b> Thiết lập vị trí, kích thước,
     * hình ảnh cho hai trạng thái (on/off), và trạng thái ban đầu.
     * <p>
     * <b>Expected:</b> Nút bấm được tạo
     * với trạng thái {@code initialState}.
     *
     * @param x            Tọa độ X.
     * @param y            Tọa độ Y.
     * @param width        Chiều rộng.
     * @param height       Chiều cao.
     * @param onImage      Ảnh khi trạng thái là "on".
     * @param offImage     Ảnh khi trạng thái là "off".
     * @param initialState Trạng thái ban đầu (true = on, false = off).
     */
    public ToggleButton(double x, double y, double width, double height,
                        Image onImage, Image offImage, boolean initialState) {
        super(x, y, width, height, null);
        this.onImage = onImage;
        this.offImage = offImage;
        this.isOn = initialState;
    }

    /**
     * Khởi tạo ToggleButton với kích thước mặc định
     * từ {@link GameConstants}.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor chính với kích thước
     * ({@code UI_BUTTON_WIDTH}, {@code UI_BUTTON_HEIGHT}) mặc định.
     * <p>
     * <b>Expected:</b> Nút bấm được tạo
     * với kích thước mặc định.
     *
     * @param x            Tọa độ X.
     * @param y            Tọa độ Y.
     * @param onImage      Ảnh khi trạng thái là "on".
     * @param offImage     Ảnh khi trạng thái là "off".
     * @param initialState Trạng thái ban đầu (true = on, false = off).
     */
    public ToggleButton(double x, double y, Image onImage, Image offImage, boolean initialState) {
        this(x, y, GameConstants.UI_BUTTON_WIDTH, GameConstants.UI_BUTTON_HEIGHT,
                onImage, offImage, initialState);
    }

    /**
     * Xử lý input và đảo ngược (toggle) trạng thái nếu được click.
     * <p>
     * <b>Định nghĩa:</b> Gọi {@code super.handleInput}
     * (để xử lý hover, click).
     * Nếu {@code isClicked} là true,
     * đảo ngược giá trị của {@code isOn}.
     * <p>
     * <b>Expected:</b> {@code isOn}
     * được lật (false -> true, true -> false)
     * khi người dùng click vào nút.
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    @Override
    public void handleInput(I_InputProvider inputProvider) {
        super.handleInput(inputProvider);

        if (isClicked) {
            this.isOn = !this.isOn;
        }
    }

    /**
     * Vẽ (render) nút bấm dựa trên trạng thái hiện tại.
     * <p>
     * <b>Định nghĩa:</b> Vẽ {@code onImage}
     * nếu {@code isOn} là true.
     * Vẽ {@code offImage} nếu {@code isOn} là false.
     * <p>
     * <b>Expected:</b> Hình ảnh của nút
     * được vẽ chính xác theo trạng thái (on/off).
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    @Override
    public void renderDefault(GraphicsContext gc) {
        Image imgToDraw = isOn ? onImage : offImage;

        if (imgToDraw != null) {
            gc.drawImage(imgToDraw, x, y, width, height);
        }
    }

    /**
     * Lấy trạng thái hiện tại của nút.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code isOn}.
     * <p>
     * <b>Expected:</b> {@code true} nếu nút đang "on",
     * {@code false} nếu đang "off".
     *
     * @return boolean Trạng thái hiện tại.
     */
    public boolean isOn() {
        return this.isOn;
    }
}