package org.example.gamelogic.states;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.config.GameConstants;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.events.ChangeStateEvent;

public final class MainMenuState implements GameState {
    private final String title = "ARKANOID";
    private final String[] menuItems = {"Start Game", "Exit"};
    private int currentSelection = 0;
    private final Font titleFont = new Font("Arial", 60);
    private final Font menuFont = new Font("Arial", 30);
    private final Rectangle2D[] menuBounds;

    public MainMenuState() {
        this.menuBounds = new Rectangle2D[menuItems.length];
        double menuStartX = GameConstants.SCREEN_WIDTH / 2.0 - 100; // Center buttons
        double menuStartY = GameConstants.SCREEN_HEIGHT / 2.0;
        double buttonHeight = 50;
        double buttonSpacing = 10;

        for (int i = 0; i < menuItems.length; i++) {
            menuBounds[i] = new Rectangle2D(
                    menuStartX,
                    menuStartY + i * (buttonHeight + buttonSpacing),
                    200, // Button width
                    buttonHeight
            );
        }
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.DARKSLATEBLUE);
        gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        gc.setFont(titleFont);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(title, GameConstants.SCREEN_WIDTH / 2.0, 150);

        gc.setFont(menuFont);
        gc.setTextAlign(TextAlignment.CENTER); // Căn giữa chữ trong nút
        for (int i = 0; i < menuItems.length; i++) {
            Rectangle2D bounds = menuBounds[i];
            // Vẽ nền nút (tùy chọn)
            // gc.setFill(Color.GRAY);
            // gc.fillRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());

            // Đổi màu chữ nếu đang được chọn
            if (i == currentSelection) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.WHITE);
            }
            // Vẽ chữ
            gc.fillText(menuItems[i],
                    bounds.getMinX() + bounds.getWidth() / 2.0, // Căn giữa chữ theo X
                    bounds.getMinY() + bounds.getHeight() / 1.5); // Căn giữa chữ theo Y (ước lượng)
        }
    }

    @Override
    public void handleInput(I_InputProvider input) {
        double mouseX = input.getMouseX();
        double mouseY = input.getMouseY();
        boolean itemHovered = false;

        for (int i = 0; i < menuItems.length; i++) {
            if (menuBounds[i].contains(mouseX, mouseY)) {
                currentSelection = i;
                itemHovered = true;
                break;
            }
        }
        if (!itemHovered) {
            currentSelection = -1;
        }

        if ( (input.isKeyPressed(KeyCode.ENTER) || input.isMouseClicked()) && currentSelection != -1) {
            selectMenuItem(currentSelection);
        }
    }

    private void selectMenuItem(int index) {
        switch (index) {
            case 0: // Start Game
                // Phát sự kiện yêu cầu chuyển sang PlayingState
                EventManager.getInstance().publish(new ChangeStateEvent(GameStateEnum.PLAYING));
                break;
            case 1: // Exit
                System.exit(0);
                break;
        }
    }
}
