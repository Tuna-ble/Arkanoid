package org.example.gamelogic.graphics.text;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.gamelogic.I_InputProvider;

import java.util.HashSet;
import java.util.Set;

/**
 * Ô nhập text đơn giản:
 * - Vẽ khung + nền mờ
 * - Placeholder khi rỗng
 * - Hỗ trợ passwordMode (hiển thị ***)
 * - Tự xử lý nhập ký tự, Backspace, focus bằng chuột
 */
public final class InputBox {

    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final int maxLength;

    private final String placeholder;
    private final boolean passwordMode;

    private final StringBuilder text = new StringBuilder();
    private boolean focused = false;
    private boolean hovered = false;

    // Debounce key: mỗi lần nhấn chỉ xử lý 1 lần
    private final Set<KeyCode> handledKeys = new HashSet<>();

    public InputBox(double x,
                    double y,
                    double width,
                    double height,
                    String placeholder,
                    boolean passwordMode,
                    int maxLength) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.placeholder = placeholder != null ? placeholder : "";
        this.passwordMode = passwordMode;
        this.maxLength = maxLength > 0 ? maxLength : 32;
    }

    public void render(GraphicsContext gc) {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Arial", 16));

        // Khung
        gc.setLineWidth(1.5);
        gc.setStroke(focused ? Color.web("#66ccff") : Color.WHITE);
        gc.strokeRoundRect(x, y, width, height, 8, 8);

        // Nền mờ
        gc.setFill(Color.color(0, 0, 0, 0.4));
        gc.fillRoundRect(x, y, width, height, 8, 8);

        // Text / placeholder
        String displayText;
        if (text.length() == 0) {
            if (!hovered) {
                // Chưa có text + không hover -> hiện placeholder mờ
                gc.setFill(Color.color(1, 1, 1, 0.5));
                displayText = placeholder;
            } else {
                // Hover vào box -> placeholder biến mất
                displayText = "";
            }
        } else {
            gc.setFill(Color.WHITE);
            if (passwordMode) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    sb.append('*');
                }
                displayText = sb.toString();
            } else {
                displayText = text.toString();
            }
        }

        gc.fillText(displayText, x + 8, y + height / 2.0 + 5);
    }

    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) {
            return;
        }

        // Chuột: chọn focus
        if (inputProvider.isMouseClicked()) {
            int mx = inputProvider.getMouseX();
            int my = inputProvider.getMouseY();
            hovered = isInside(mx, my);
            focused = isInside(mx, my);
        }

        if (!focused) {
            // Nếu không focus thì chỉ cần clear debounce cho key đã nhả
            var pressed = inputProvider.getPressedKeys();
            handledKeys.removeIf(k -> !pressed.contains(k));
            return;
        }

        // Bàn phím
        var pressed = inputProvider.getPressedKeys();
        handledKeys.removeIf(k -> !pressed.contains(k));

        for (KeyCode key : pressed) {
            if (handledKeys.contains(key)) {
                continue;
            }
            handledKeys.add(key);

            if (key == KeyCode.BACK_SPACE) {
                if (text.length() > 0) {
                    text.deleteCharAt(text.length() - 1);
                }
                continue;
            }

            // Enter / Tab để state xử lý, không thêm text
            if (key == KeyCode.ENTER || key == KeyCode.TAB) {
                continue;
            }

            // Các ký tự cho phép
            char ch = 0;
            if (key.isLetterKey()) {
                String name = key.getName();
                if (name != null && name.length() == 1) {
                    ch = Character.toLowerCase(name.charAt(0));
                }
            } else if (key.isDigitKey()) {
                String name = key.getName();
                if (name != null && name.length() == 1) {
                    ch = name.charAt(0);
                }
            } else if (key == KeyCode.SPACE) {
                ch = ' ';
            } else if (key == KeyCode.MINUS || key == KeyCode.UNDERSCORE) {
                ch = '_';
            }

            if (ch != 0 && text.length() < maxLength) {
                text.append(ch);
            }
        }
    }

    private boolean isInside(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    // --------- Getter / Setter ---------

    public String getText() {
        return text.toString();
    }

    public void setText(String value) {
        text.setLength(0);
        if (value != null) {
            if (value.length() > maxLength) {
                text.append(value, 0, maxLength);
            } else {
                text.append(value);
            }
        }
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }
}
