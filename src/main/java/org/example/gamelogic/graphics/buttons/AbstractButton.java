package org.example.gamelogic.graphics.buttons;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;

/**
 * Lớp cơ sở (abstract class) cho các nút bấm (Button) có tương tác.
 * <p>
 * Kế thừa từ {@link AbstractUIElement} và bổ sung logic
 * xử lý trạng thái (hover, click), văn bản (text),
 * và màu sắc (colors).
 */
public abstract class AbstractButton extends AbstractUIElement{
    protected String text;
    protected Font font;
    protected boolean isHovered;
    protected boolean isClicked;

    protected Color backgroundColor;
    protected Color hoverBackgroundColor;
    protected Color strokeColor;
    protected Color hoverStrokeColor;
    protected Color textColor;

    private double timer = 0.0;
    private final double DURATION = 0.5;

    /**
     * Khởi tạo một nút bấm (AbstractButton) cơ sở.
     * <p>
     * <b>Định nghĩa:</b> Gọi constructor của `super`
     * ({@link AbstractUIElement}),
     * lưu văn bản ({@code text}),
     * và thiết lập các màu sắc/font chữ mặc định.
     * <p>
     * <b>Expected:</b> Nút bấm được tạo với các
     * giá trị mặc định,
     * {@code isHovered} và {@code isClicked} là {@code false}.
     *
     * @param x      Tọa độ X.
     * @param y      Tọa độ Y.
     * @param width  Chiều rộng.
     * @param height Chiều cao.
     * @param text   Văn bản (String) để hiển thị (có thể là {@code null}).
     */
    public AbstractButton(double x, double y, double width, double height, String text) {
        super(x, y, width, height);
        this.text = text;
        this.font = AssetManager.getInstance().getFont("Anxel", 25);
        this.isHovered = false;
        this.isClicked = false;

        this.backgroundColor = Color.color(0.13, 0.13, 0.13, 0.5);
        this.hoverBackgroundColor = Color.color(0.2, 0.2, 0.2, 0.7);
        this.strokeColor = Color.color(1, 1, 1, 0.8);
        this.hoverStrokeColor = Color.WHITE;
        this.textColor = Color.WHITE;
    }

    /**
     * Xử lý input (hover, click) cho nút bấm.
     * <p>
     * <b>Định nghĩa:</b> Ghi đè phương thức từ
     * {@link AbstractUIElement}.
     * Tính toán {@code isHovered} bằng cách kiểm tra
     * tọa độ chuột có nằm trong vùng (bounds) của nút hay không.
     * Tính toán {@code isClicked} nếu {@code isHovered}
     * và chuột được click.
     * <p>
     * <b>Expected:</b> Trạng thái {@code isHovered}
     * và {@code isClicked}
     * được cập nhật chính xác dựa trên input của người dùng
     * (trừ khi nút bị vô hiệu hóa - {@code isDisabled}).
     *
     * @param inputProvider Nguồn cung cấp input (phím, chuột).
     */
    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null || isDisabled()) {
            isHovered = false;
            isClicked = false;
            return;
        }

        int mouseX = inputProvider.getMouseX();
        int mouseY = inputProvider.getMouseY();

        isHovered = mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;

        isClicked = isHovered && inputProvider.isMouseClicked();
    }

    /**
     * (Abstract) Vẽ trạng thái mặc định (tĩnh) của nút bấm.
     * <p>
     * <b>Định nghĩa:</b> Phương thức bắt buộc (abstract)
     * mà các lớp con (subclass) phải implement
     * để vẽ hình ảnh/văn bản của chúng.
     * <p>
     * <b>Expected:</b> Lớp con sẽ vẽ
     * trạng thái trực quan của nó lên {@code gc}.
     *
     * @param gc Context (bút vẽ) của canvas.
     */
    public abstract void renderDefault(GraphicsContext gc);

    /**
     * Kiểm tra xem nút có đang được di chuột qua (hover) không.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code isHovered}.
     * <p>
     * <b>Expected:</b> {@code true} nếu chuột đang ở trên nút,
     * ngược lại {@code false}.
     *
     * @return boolean Trạng thái hover.
     */
    public boolean isHovered() {
        return isHovered;
    }

    /**
     * Kiểm tra xem nút có vừa được click không.
     * <p>
     * <b>Định nghĩa:</b> Trả về giá trị của {@code isClicked}
     * (được tính toán trong {@code handleInput}).
     * <p>
     * <b>Expected:</b> {@code true} nếu nút được hover
     * và chuột vừa click, ngược lại {@code false}.
     *
     * @return boolean Trạng thái click.
     */
    public boolean isClicked() {
        return isClicked;
    }

    /**
     * (Helper) Kiểm tra xem một tọa độ (x, y)
     * có nằm trong vùng (bounds) của nút không.
     * <p>
     * <b>Định nghĩa:</b> Thực hiện phép toán
     * kiểm tra va chạm AABB (Axis-Aligned Bounding Box).
     * <p>
     * <b>Expected:</b> {@code true} nếu tọa độ nằm trong nút,
     * ngược lại {@code false}.
     *
     * @param mouseX Tọa độ X cần kiểm tra.
     * @param mouseY Tọa độ Y cần kiểm tra.
     * @return boolean True nếu tọa độ nằm trong.
     */
    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    /**
     * Lấy văn bản (text) của nút.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code text}.
     * <p>
     * <b>Expected:</b> Chuỗi (String) văn bản của nút.
     *
     * @return String Văn bản.
     */
    public String getText() {
        return text;
    }

    /**
     * Đặt văn bản (text) cho nút.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code text}.
     * <p>
     * <b>Expected:</b> {@code text} của nút được thay đổi.
     *
     * @param text Văn bản mới.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Lấy font chữ của nút.
     * <p>
     * <b>Định nghĩa:</b> Trả về {@code font}.
     * <p>
     * <b>Expected:</b> Đối tượng {@link Font} hiện tại.
     *
     * @return Font chữ.
     */
    public Font getFont() {
        return font;
    }

    /**
     * Đặt font chữ cho nút.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật {@code font}.
     * <p>
     * <b>Expected:</b> {@code font} của nút được thay đổi.
     *
     * @param font Font chữ mới.
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Đặt (ghi đè) các màu sắc mặc định của nút.
     * <p>
     * <b>Định nghĩa:</b> Cập nhật các thuộc tính
     * màu (background, stroke, text)
     * cho cả trạng thái thường và hover.
     * <p>
     * <b>Expected:</b> Nút sẽ sử dụng các màu mới
     * này khi render (nếu lớp con có sử dụng chúng).
     *
     * @param backgroundColor      Màu nền mặc định.
     * @param hoverBackgroundColor Màu nền khi hover.
     * @param strokeColor          Màu viền mặc định.
     * @param hoverStrokeColor     Màu viền khi hover.
     * @param textColor            Màu văn bản.
     */
    public void setColors(Color backgroundColor, Color hoverBackgroundColor,
                          Color strokeColor, Color hoverStrokeColor, Color textColor) {
        this.backgroundColor = backgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor;
        this.strokeColor = strokeColor;
        this.hoverStrokeColor = hoverStrokeColor;
        this.textColor = textColor;
    }
}