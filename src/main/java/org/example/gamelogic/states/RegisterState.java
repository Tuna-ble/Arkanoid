package org.example.gamelogic.states;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.DropShadow;
import org.example.config.GameConstants;
import org.example.data.AssetManager;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.text.TextRenderer;
import org.example.gamelogic.graphics.text.InputBox;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;
import org.example.data.SaveGameRepository;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public final class RegisterState implements GameState {

    private static final String ACCOUNT_DIRECTORY = "accounts";
    private static final String ACCOUNT_FILE_PATH =
            ACCOUNT_DIRECTORY + File.separator + "accounts.txt";
    private static final String SAVE_ROOT_DIRECTORY = "saves";

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private Image backgroundImage;
    private Image normalImage;
    private Image hoveredImage;

    private AbstractButton registerButton;
    private AbstractButton backButton;

    private double elapsedTime = 0;

    private enum ActiveField {
        USERNAME,
        PASSWORD,
        CONFIRM_PASSWORD
    }

    private ActiveField activeField = ActiveField.USERNAME;

    private String usernameInput = "";
    private String passwordInput = "";
    private String confirmPasswordInput = "";

    private static final int MAX_FIELD_LENGTH = 20;

    // Vị trí ô input (dùng chung cho render + handleInput)
    private final double fieldStartX = GameConstants.SCREEN_WIDTH / 2.0 - 200;
    private final double fieldWidth = 400;
    private final double fieldHeight = 32;
    private final double usernameY = 240;
    private final double passwordY = 285;
    private final double confirmY = 330;

    private final InputBox usernameBox;
    private final InputBox passwordBox;
    private final InputBox confirmPasswordBox;

    private String message = "";
    private Color messageColor = Color.RED;

    private final Set<KeyCode> handledKeys = new HashSet<>();

    public RegisterState() {
        AssetManager am = AssetManager.getInstance();
        this.backgroundImage = am.getImage("mainMenu"); // có thể đổi sau
        this.normalImage = am.getImage("button");
        this.hoveredImage = am.getImage("hoveredButton");

        double baseY = GameConstants.SCREEN_HEIGHT / 2.0 + 40;
        double buttonGap = 70;

        registerButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                baseY + (buttonGap * 0),
                normalImage,
                hoveredImage,
                "Register"
        );

        backButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                baseY + (buttonGap * 1),
                normalImage,
                hoveredImage,
                "Back"
        );

        usernameBox = new InputBox(
                fieldStartX,
                usernameY,
                fieldWidth,
                fieldHeight,
                "Enter username",
                false,
                MAX_FIELD_LENGTH
        );
        passwordBox = new InputBox(
                fieldStartX,
                passwordY,
                fieldWidth,
                fieldHeight,
                "Enter password",
                true,
                MAX_FIELD_LENGTH
        );
        confirmPasswordBox = new InputBox(
                fieldStartX,
                confirmY,
                fieldWidth,
                fieldHeight,
                "Confirm password",
                true,
                MAX_FIELD_LENGTH
        );
    }

    @Override
    public void update(double deltaTime) {
        elapsedTime += deltaTime;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setTransform(new Affine());
        gc.setTextAlign(TextAlignment.LEFT);
        gc.clearRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        // Background
        if (backgroundImage != null) {
            gc.drawImage(
                    backgroundImage,
                    0,
                    0,
                    GameConstants.SCREEN_WIDTH,
                    GameConstants.SCREEN_HEIGHT
            );
        } else {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
        }

        // Title
        gc.setTextAlign(TextAlignment.CENTER);
        LinearGradient titleFill = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#00b4ff")),
                new Stop(1, Color.web("#7b2cff"))
        );
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.color(0, 0, 0, 0.8));
        titleShadow.setRadius(20);
        titleShadow.setOffsetY(6);

        gc.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 40));
        gc.setEffect(titleShadow);
        gc.setFill(Color.color(0, 0, 0, 0.8));
        gc.fillText("CREATE ACCOUNT", centerX + 3, 130 + 3);

        gc.setEffect(null);
        gc.setFill(titleFill);
        gc.fillText("CREATE ACCOUNT", centerX, 130);

        // Description
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", 16));
        gc.setFill(Color.color(1, 1, 1, 0.85));
        gc.fillText("Create a new profile to save your progress",
                centerX, 165);

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Arial", 16));

        gc.setFill(Color.WHITE);
        usernameBox.render(gc);
        passwordBox.render(gc);
        confirmPasswordBox.render(gc);

        // Buttons
        if (registerButton != null) {
            registerButton.render(gc);
        }
        if (backButton != null) {
            backButton.render(gc);
        }

        // Message
        if (message != null && !message.isEmpty()) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(messageColor);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText(message, centerX, confirmY + 60);
        }

        // Footer hint
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", 14));
        gc.setFill(Color.color(1, 1, 1, 0.7));
        gc.fillText("Press ESC to exit", centerX, GameConstants.SCREEN_HEIGHT - 30);
    }

    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) {
            return;
        }

        // 1. Box
        usernameBox.handleInput(inputProvider);
        passwordBox.handleInput(inputProvider);
        confirmPasswordBox.handleInput(inputProvider);

        // 2. Button
        updateButtons(inputProvider);

        if (registerButton != null && registerButton.isClicked()) {
            handleRegister();
        } else if (backButton != null && backButton.isClicked()) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.BEGIN)
            );
            return;
        }

        // 3. ESC để quay lại
        if (inputProvider.isKeyPressed(KeyCode.ESCAPE)) {
            EventManager.getInstance().publish(
                    new ChangeStateEvent(GameStateEnum.BEGIN)
            );
            return;
        }

        // 4. ENTER để đăng ký
        Set<KeyCode> pressed = inputProvider.getPressedKeys();
        handledKeys.removeIf(key -> !pressed.contains(key));
        for (KeyCode key : pressed) {
            if (handledKeys.contains(key)) {
                continue;
            }
            handledKeys.add(key);
            if (key == KeyCode.ENTER) {
                handleRegister();
            }
        }
    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (registerButton != null) {
            registerButton.handleInput(inputProvider);
        }
        if (backButton != null) {
            backButton.handleInput(inputProvider);
        }
    }

    private void handleMouseFocus(I_InputProvider inputProvider) {
        if (!inputProvider.isMouseClicked()) {
            return;
        }

        int mx = inputProvider.getMouseX();
        int my = inputProvider.getMouseY();

        if (isInsideBox(mx, my, fieldStartX, usernameY, fieldWidth, fieldHeight)) {
            activeField = ActiveField.USERNAME;
        } else if (isInsideBox(mx, my, fieldStartX, passwordY, fieldWidth, fieldHeight)) {
            activeField = ActiveField.PASSWORD;
        } else if (isInsideBox(mx, my, fieldStartX, confirmY, fieldWidth, fieldHeight)) {
            activeField = ActiveField.CONFIRM_PASSWORD;
        }
    }

    private boolean isInsideBox(double x, double y, double bx, double by, double w, double h) {
        return x >= bx && x <= bx + w && y >= by && y <= by + h;
    }

    private void handleKeyboardInput(I_InputProvider inputProvider) {
        Set<KeyCode> pressed = inputProvider.getPressedKeys();

        // Xoá những key đã nhả ra khỏi handledKeys
        handledKeys.removeIf(key -> !pressed.contains(key));

        for (KeyCode key : pressed) {
            if (handledKeys.contains(key)) {
                continue;
            }
            handledKeys.add(key);
            if (key == KeyCode.TAB) {
                switchToNextField();
                continue;
            }
            if (key == KeyCode.ENTER) {
                handleRegister();
                continue;
            }
            if (key == KeyCode.BACK_SPACE) {
                handleBackspace();
                continue;
            }

            // Các phím chữ/số
            handleCharacterKey(key);
        }
    }

    private void switchToNextField() {
        switch (activeField) {
            case USERNAME:
                activeField = ActiveField.PASSWORD;
                break;
            case PASSWORD:
                activeField = ActiveField.CONFIRM_PASSWORD;
                break;
            case CONFIRM_PASSWORD:
                activeField = ActiveField.USERNAME;
                break;
            default:
                break;
        }
    }

    private void handleBackspace() {
        switch (activeField) {
            case USERNAME:
                if (!usernameInput.isEmpty()) {
                    usernameInput = usernameInput.substring(0, usernameInput.length() - 1);
                }
                break;
            case PASSWORD:
                if (!passwordInput.isEmpty()) {
                    passwordInput = passwordInput.substring(0, passwordInput.length() - 1);
                }
                break;
            case CONFIRM_PASSWORD:
                if (!confirmPasswordInput.isEmpty()) {
                    confirmPasswordInput = confirmPasswordInput.substring(0, confirmPasswordInput.length() - 1);
                }
                break;
            default:
                break;
        }
    }

    private void handleCharacterKey(KeyCode key) {
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

        if (ch == 0) {
            return;
        }

        appendCharToActiveField(ch);
    }

    private void appendCharToActiveField(char ch) {
        switch (activeField) {
            case USERNAME:
                if (usernameInput.length() < MAX_FIELD_LENGTH) {
                    usernameInput += ch;
                }
                break;
            case PASSWORD:
                if (passwordInput.length() < MAX_FIELD_LENGTH) {
                    passwordInput += ch;
                }
                break;
            case CONFIRM_PASSWORD:
                if (confirmPasswordInput.length() < MAX_FIELD_LENGTH) {
                    confirmPasswordInput += ch;
                }
                break;
            default:
                break;
        }
    }

    // ------------------- Logic đăng ký -------------------

    private void handleRegister() {
        String username = usernameBox.getText() == null ? "" : usernameBox.getText().trim();
        String password = passwordBox.getText() == null ? "" : passwordBox.getText().trim();
        String confirm = confirmPasswordBox.getText() == null ? "" : confirmPasswordBox.getText().trim();

        // Validate cơ bản
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            setErrorMessage("Username và mật khẩu không được để trống.");
            return;
        }

        if (!password.equals(confirm)) {
            setErrorMessage("Mật khẩu nhập lại không khớp.");
            return;
        }

        if (username.contains(":")) {
            setErrorMessage("Username không được chứa dấu ':'");
            return;
        }

        ensureAccountDirectoryExists();

        // Kiểm tra username đã tồn tại chưa
        if (isUsernameExists(username)) {
            setErrorMessage("Username đã tồn tại, hãy chọn tên khác.");
            return;
        }

        // Ghi account vào file
        if (!appendAccountToFile(username, password)) {
            setErrorMessage("Lỗi lưu tài khoản, hãy thử lại.");
            return;
        }

        // Tạo thư mục saves riêng cho user
        createUserSaveDirectory(username);

        // Lưu user hiện tại vào repository
        GameManager gm = GameManager.getInstance();
        if (gm != null && gm.getSaveGameRepository() != null) {
            gm.getSaveGameRepository().setCurrentAccountId(username);
        }

        setSuccessMessage("Đăng ký thành công! Nhấn BACK để đăng nhập.");

        System.out.println("User registered: " + username);
    }

    private void ensureAccountDirectoryExists() {
        File dir = new File(ACCOUNT_DIRECTORY);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Không thể tạo thư mục accounts.");
            }
        }
    }

    private boolean isUsernameExists(String username) {
        File accountFile = new File(ACCOUNT_FILE_PATH);
        if (!accountFile.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(accountFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file accounts: " + e.getMessage());
        }
        return false;
    }

    private boolean appendAccountToFile(String username, String password) {
        File accountFile = new File(ACCOUNT_FILE_PATH);
        try {
            if (!accountFile.exists()) {
                ensureAccountDirectoryExists();
                boolean created = accountFile.createNewFile();
                if (!created) {
                    System.err.println("Không thể tạo file accounts.txt");
                    return false;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(accountFile, true))) {
                writer.write(username + ":" + password);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi ghi file accounts: " + e.getMessage());
            return false;
        }
    }

    private void createUserSaveDirectory(String username) {
        File root = new File(SAVE_ROOT_DIRECTORY);
        if (!root.exists()) {
            boolean createdRoot = root.mkdirs();
            if (!createdRoot) {
                System.err.println("Không thể tạo thư mục saves root.");
                return;
            }
        }

        File userDir = new File(root, username);
        if (!userDir.exists()) {
            boolean createdUserDir = userDir.mkdirs();
            if (createdUserDir) {
                System.out.println("Đã tạo thư mục save cho user: " + username);
            } else {
                System.err.println("Không thể tạo thư mục save cho user: " + username);
            }
        }
    }

    private void setErrorMessage(String msg) {
        this.message = msg;
        this.messageColor = Color.RED;
    }

    private void setSuccessMessage(String msg) {
        this.message = msg;
        this.messageColor = Color.LIGHTGREEN;
    }

    private String maskPassword(String pwd) {
        if (pwd == null || pwd.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pwd.length(); i++) {
            sb.append('*');
        }
        return sb.toString();
    }
}
