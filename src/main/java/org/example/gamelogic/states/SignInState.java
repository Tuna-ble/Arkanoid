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
import org.example.data.SaveGameRepository;
import org.example.gamelogic.I_InputProvider;
import org.example.gamelogic.core.EventManager;
import org.example.gamelogic.core.GameManager;
import org.example.gamelogic.events.ChangeStateEvent;
import org.example.gamelogic.graphics.text.TextRenderer;
import org.example.gamelogic.graphics.text.InputBox;
import org.example.gamelogic.graphics.buttons.AbstractButton;
import org.example.gamelogic.graphics.buttons.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class SignInState implements GameState {

    private static final String ACCOUNT_DIRECTORY = "accounts";
    private static final String ACCOUNT_FILE_PATH =
            ACCOUNT_DIRECTORY + File.separator + "accounts.txt";
    private static final String SAVE_ROOT_DIRECTORY = "saves";

    private final double centerX = GameConstants.SCREEN_WIDTH / 2.0;

    private Image backgroundImage;
    private Image normalImage;
    private Image hoveredImage;

    private AbstractButton signInButton;
    private AbstractButton backButton;

    private double elapsedTime = 0;

    private enum ActiveField {
        USERNAME,
        PASSWORD
    }

    private ActiveField activeField = ActiveField.USERNAME;

    private String usernameInput = "";
    private String passwordInput = "";

    private static final int MAX_FIELD_LENGTH = 20;

    private final double fieldStartX = GameConstants.SCREEN_WIDTH / 2.0 - 200;
    private final double fieldWidth = 400;
    private final double fieldHeight = 32;
    private final double usernameY = 250;
    private final double passwordY = 300;

    private final InputBox usernameBox;
    private final InputBox passwordBox;

    private String message = "";
    private Color messageColor = Color.RED;

    private final Set<KeyCode> handledKeys = new HashSet<>();

    public SignInState() {
        AssetManager am = AssetManager.getInstance();
        this.backgroundImage = am.getImage("mainMenu"); // có thể đổi sau
        this.normalImage = am.getImage("button");
        this.hoveredImage = am.getImage("hoveredButton");

        double baseY = GameConstants.SCREEN_HEIGHT / 2.0 + 40;
        double buttonGap = 70;

        signInButton = new Button(
                centerX - GameConstants.UI_BUTTON_WIDTH / 2,
                baseY + (buttonGap * 0),
                normalImage,
                hoveredImage,
                "Sign In"
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
        //gc.setEffect(titleShadow);
        gc.setFill(Color.color(0, 0, 0, 0.8));
        gc.fillText("SIGN IN", centerX + 3, 130 + 3);

        //gc.setEffect(null);
        gc.setFill(titleFill);
        gc.fillText("SIGN IN", centerX, 130);

        // Description
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", 16));
        gc.setFill(Color.color(1, 1, 1, 0.85));
        gc.fillText("Log in to your profile to continue",
                centerX, 165);

        // Input labels + boxes
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("Arial", 16));

        gc.setFill(Color.WHITE);
        usernameBox.render(gc);
        passwordBox.render(gc);

        // Buttons
        if (signInButton != null) {
            signInButton.render(gc);
        }
        if (backButton != null) {
            backButton.render(gc);
        }

        // Message
        if (message != null && !message.isEmpty()) {
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(messageColor);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText(message, centerX, passwordY + 60);
        }

        // Footer
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", 14));
        gc.setFill(Color.color(1, 1, 1, 0.7));
        gc.fillText("Press ESC to exit", centerX, GameConstants.SCREEN_HEIGHT - 30);
    }

    public void handleInput(I_InputProvider inputProvider) {
        if (inputProvider == null) {
            return;
        }

        // 1. InputBox
        usernameBox.handleInput(inputProvider);
        passwordBox.handleInput(inputProvider);

        // 2. Buttons
        updateButtons(inputProvider);

        if (signInButton != null && signInButton.isClicked()) {
            handleSignIn();
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

        // 4. ENTER để đăng nhập (debounce ở cấp state)
        Set<KeyCode> pressed = inputProvider.getPressedKeys();
        handledKeys.removeIf(key -> !pressed.contains(key));
        for (KeyCode key : pressed) {
            if (handledKeys.contains(key)) {
                continue;
            }
            handledKeys.add(key);

            if (key == KeyCode.ENTER) {
                handleSignIn();
            }
        }
    }

    private void updateButtons(I_InputProvider inputProvider) {
        if (signInButton != null) {
            signInButton.handleInput(inputProvider);
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
        }
    }

    private boolean isInsideBox(double x, double y,
                                double bx, double by,
                                double w, double h) {
        return x >= bx && x <= bx + w && y >= by && y <= by + h;
    }

    private void handleKeyboardInput(I_InputProvider inputProvider) {
        Set<KeyCode> pressed = inputProvider.getPressedKeys();

        // remove released keys
        handledKeys.removeIf(key -> !pressed.contains(key));

        for (KeyCode key : pressed) {
            if (handledKeys.contains(key)) {
                continue;
            }
            handledKeys.add(key);

            // control keys
            if (key == KeyCode.TAB) {
                switchField();
                continue;
            }
            if (key == KeyCode.ENTER) {
                handleSignIn();
                continue;
            }
            if (key == KeyCode.BACK_SPACE) {
                handleBackspace();
                continue;
            }

            // character keys
            handleCharacterKey(key);
        }
    }

    private void switchField() {
        if (activeField == ActiveField.USERNAME) {
            activeField = ActiveField.PASSWORD;
        } else {
            activeField = ActiveField.USERNAME;
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
        } else if (key == KeyCode.UNDERSCORE || key == KeyCode.MINUS) {
            ch = '_';
        }

        if (ch == 0) {
            return;
        }

        if (activeField == ActiveField.USERNAME) {
            if (usernameInput.length() < MAX_FIELD_LENGTH) {
                usernameInput += ch;
            }
        } else if (activeField == ActiveField.PASSWORD) {
            if (passwordInput.length() < MAX_FIELD_LENGTH) {
                passwordInput += ch;
            }
        }
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

    private void handleSignIn() {
        String username = usernameBox.getText() == null ? "" : usernameBox.getText().trim();
        String password = passwordBox.getText() == null ? "" : passwordBox.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            setErrorMessage("Username và mật khẩu không được để trống.");
            return;
        }

        ensureAccountDirectoryExists();

        if (!checkCredentials(username, password)) {
            setErrorMessage("Sai tài khoản hoặc mật khẩu.");
            return;
        }

        GameManager gm = GameManager.getInstance();
        if (gm != null && gm.getSaveGameRepository() != null) {
            gm.getSaveGameRepository().setCurrentAccountId(username);
            System.out.println("vclll");
        }

        // Đảm bảo thư mục save cho user tồn tại
        createUserSaveDirectory(username);

        setSuccessMessage("Đăng nhập thành công!");
        System.out.println("User signed in: " + username);

        EventManager.getInstance().publish(
                new ChangeStateEvent(GameStateEnum.MAIN_MENU)
        );
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

    private boolean checkCredentials(String username, String password) {
        File accountFile = new File(ACCOUNT_FILE_PATH);
        if (!accountFile.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(accountFile))) {
            String line;
            while ( (line = reader.readLine()) != null ) {
                String[] parts = line.split(":", 2);
                if (parts.length < 2) {
                    continue;
                }
                String fileUsername = parts[0];
                String filePassword = parts[1];
                if (fileUsername.equals(username) && filePassword.equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file accounts: " + e.getMessage());
        }

        return false;
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


}
