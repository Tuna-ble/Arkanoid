package org.example.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveGameRepository {

    private static final String SAVE_ROOT_DIRECTORY = "saves";
    private static final String SAVE_FILE_EXTENSION = ".sav";

    /**
     * Account hiện tại, dùng làm tên folder con trong "saves".
     * Mặc định là "default" nếu chưa đăng nhập.
     */
    private String currentAccountId = "default";

    public SaveGameRepository() {
        ensureRootDirectoryExists();
        ensureAccountDirectoryExists();
    }

    /**
     * Đặt account hiện tại. Mỗi account sẽ lưu ở thư mục riêng:
     * saves/<accountId>/
     */
    public void setCurrentAccountId(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            this.currentAccountId = "default";
        } else {
            this.currentAccountId = accountId.trim();
        }
        ensureAccountDirectoryExists();
    }

    public String getCurrentAccountId() {
        return currentAccountId;
    }

    // ----------------- Thư mục lưu -----------------

    private void ensureRootDirectoryExists() {
        File dir = new File(SAVE_ROOT_DIRECTORY);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Không thể tạo thư mục gốc saves.");
            }
        }
    }

    private File getAccountDirectory() {
        return new File(SAVE_ROOT_DIRECTORY, currentAccountId);
    }

    private void ensureAccountDirectoryExists() {
        ensureRootDirectoryExists();
        File dir = getAccountDirectory();
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Không thể tạo thư mục save cho account: " + currentAccountId);
            } else {
                System.out.println("Đã tạo thư mục save cho account: " + currentAccountId);
            }
        }
    }

    private File getSaveFile(int levelId) {
        String fileName = "level_" + levelId + SAVE_FILE_EXTENSION;
        return new File(getAccountDirectory(), fileName);
    }

    // ----------------- SAVE / LOAD / DELETE -----------------

    public boolean saveGame(SavedGameState state, int levelId) {
        ensureAccountDirectoryExists();
        File saveFile = getSaveFile(levelId);

        try (FileOutputStream fos = new FileOutputStream(saveFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(state);
            System.out.println("Game đã được lưu cho account "
                    + currentAccountId + " - level " + levelId);
            return true;

        } catch (IOException e) {
            System.err.println("LỖI khi lưu game cho account "
                    + currentAccountId + " - level " + levelId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public SavedGameState loadGame(int levelId) {
        File saveFile = getSaveFile(levelId);

        if (!saveFile.exists()) {
            System.out.println("Không tìm thấy file save cho account "
                    + currentAccountId + " - level " + levelId);
            return null;
        }

        try (FileInputStream fis = new FileInputStream(saveFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            SavedGameState state = (SavedGameState) ois.readObject();
            System.out.println("Game đã được tải cho account "
                    + currentAccountId + " - level " + levelId);
            return state;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("LỖI khi tải game cho account "
                    + currentAccountId + " - level " + levelId + ": " + e.getMessage());
            e.printStackTrace();
            deleteSave(levelId);
            return null;
        }
    }

    public boolean deleteSave(int levelId) {
        File saveFile = getSaveFile(levelId);
        if (saveFile.exists()) {
            if (saveFile.delete()) {
                System.out.println("Đã xóa file save cho account "
                        + currentAccountId + " - level " + levelId);
                return true;
            } else {
                System.err.println("Không thể xóa file save cho account "
                        + currentAccountId + " - level " + levelId);
                return false;
            }
        }
        return true;
    }

    public boolean hasSave(int levelId) {
        File saveFile = getSaveFile(levelId);
        return saveFile.exists() && saveFile.isFile();
    }
}
