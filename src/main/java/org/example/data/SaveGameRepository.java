package org.example.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveGameRepository {

    private static final String SAVE_DIRECTORY = "saves";
    private static final String SAVE_FILE_EXTENSION = ".sav";

    private void ensureSaveDirectoryExists() {
        File dir = new File(SAVE_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private File getSaveFile(int levelId) {
        String fileName = "level_" + levelId + SAVE_FILE_EXTENSION;
        return new File(SAVE_DIRECTORY, fileName);
    }

    /**
     * Lưu trạng thái game xuống file theo level.
     *
     * @param state   dữ liệu SaveGameState cần ghi
     * @param levelId level tương ứng với file save
     * @return true nếu lưu thành công, false nếu lỗi I/O
     */
    public boolean saveGame(SavedGameState state, int levelId) {
        ensureSaveDirectoryExists();
        File saveFile = getSaveFile(levelId);

        try (FileOutputStream fos = new FileOutputStream(saveFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(state);
            System.out.println("Game đã được lưu cho level " + levelId);
            return true;

        } catch (IOException e) {
            System.err.println("LỖI khi lưu game cho level " + levelId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tải dữ liệu save game từ file của level.
     *
     * @param levelId level cần load
     * @return SaveGameState đã đọc; null nếu file không tồn tại hoặc bị lỗi
     */
    public SavedGameState loadGame(int levelId) {
        File saveFile = getSaveFile(levelId);

        if (!saveFile.exists()) {
            System.out.println("Không tìm thấy file save cho level " + levelId);
            return null;
        }

        try (FileInputStream fis = new FileInputStream(saveFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            SavedGameState state = (SavedGameState) ois.readObject();
            System.out.println("Game đã được tải cho level " + levelId);
            return state;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("LỖI khi tải game cho level " + levelId + ": " + e.getMessage());
            e.printStackTrace();
            deleteSave(levelId);
            return null;
        }
    }

    /**
     * Xóa file save của level.
     *
     * @param levelId level cần xóa save
     * @return true nếu xóa thành công (hoặc file không tồn tại), false nếu xóa thất bại
     */
    public boolean deleteSave(int levelId) {
        File saveFile = getSaveFile(levelId);
        if (saveFile.exists()) {
            if (saveFile.delete()) {
                System.out.println("Đã xóa file save cho level " + levelId);
                return true;
            } else {
                System.err.println("Không thể xóa file save cho level " + levelId);
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