package org.example.gamelogic.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.example.gamelogic.core.GameManager;

public final class ProgressManager {

    // Thư mục gốc giống SaveGameRepository
    private static final String SAVE_ROOT_DIRECTORY = "saves";
    // Tên file progress cho mỗi account
    private static final String PROGRESS_FILE_NAME = "progress.properties";

    private static final int TOTAL_LEVELS = 5;

    private ProgressManager() {
        // Utility class, không cho khởi tạo
    }

    private static String getCurrentAccountId() {
        String accountId = GameManager.AccountId;
        if (accountId == null || accountId.trim().isEmpty()) {
            return "default";
        }
        return accountId.trim();
    }

    /**
     * Đảm bảo thư mục gốc "saves" tồn tại.
     */
    private static File getRootDirectory() {
        File dir = new File(SAVE_ROOT_DIRECTORY);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Không thể tạo thư mục gốc saves cho progress.");
            }
        }
        return dir;
    }

    /**
     * Thư mục của account hiện tại: saves/<accountId>
     */
    private static File getAccountDirectory() {
        File root = getRootDirectory();
        String accountId = getCurrentAccountId();
        File dir = new File(root, accountId);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Không thể tạo thư mục progress cho account: " + accountId);
            } else {
                System.out.println("Đã tạo thư mục progress cho account: " + accountId);
            }
        }
        return dir;
    }

    /**
     * File progress cho account hiện tại: saves/<accountId>/progress.properties
     */
    private static File getProgressFile() {
        return new File(getAccountDirectory(), PROGRESS_FILE_NAME);
    }

    // =========================
    // CÁC METHOD CŨ (GIỮ NGUYÊN SIGNATURE)
    // =========================

    public static Map<Integer, Integer> loadStars() {
        Map<Integer, Integer> starsMap = new HashMap<>();
        Properties props = new Properties();
        File progressFile = getProgressFile(); // ĐỔI Ở ĐÂY

        if (!progressFile.exists()) {
            return starsMap;
        }

        try (InputStream input = new FileInputStream(progressFile)) {
            props.load(input);
            for (int i = 1; i <= TOTAL_LEVELS; i++) {
                String key = "level." + i + ".stars";
                int stars = Integer.parseInt(props.getProperty(key, "0"));
                if (stars > 0) {
                    starsMap.put(i, stars);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi khi đọc file progress: " + e.getMessage());
        }
        return starsMap;
    }

    public static int getMaxLevelUnlocked() {
        Properties props = new Properties();
        File progressFile = getProgressFile(); // ĐỔI Ở ĐÂY
        int maxLevel = 1;

        if (progressFile.exists()) {
            try (InputStream input = new FileInputStream(progressFile)) {
                props.load(input);
                maxLevel = Integer.parseInt(props.getProperty("level.unlocked", "1"));
            } catch (IOException | NumberFormatException e) {
                System.err.println("Lỗi khi đọc max level: " + e.getMessage());
            }
        }
        return Math.min(maxLevel, TOTAL_LEVELS);
    }

    public static void saveProgress(int levelCompleted, int starsAwarded) {
        if (starsAwarded < 1) {
            return;
        }

        Properties props = new Properties();
        File progressFile = getProgressFile(); // ĐỔI Ở ĐÂY

        if (progressFile.exists()) {
            try (InputStream input = new FileInputStream(progressFile)) {
                props.load(input);
            } catch (IOException e) {
                System.err.println("Không thể tải progress cũ: " + e.getMessage());
            }
        }

        String starKey = "level." + levelCompleted + ".stars";
        int oldStars = Integer.parseInt(props.getProperty(starKey, "0"));
        if (starsAwarded > oldStars) {
            props.setProperty(starKey, String.valueOf(starsAwarded));
        }

        int currentMaxLevel = Integer.parseInt(props.getProperty("level.unlocked", "1"));
        int nextLevel = levelCompleted + 1;
        if (nextLevel > currentMaxLevel && nextLevel <= TOTAL_LEVELS) {
            props.setProperty("level.unlocked", String.valueOf(nextLevel));
        }

        try (OutputStream output = new FileOutputStream(progressFile)) {
            props.store(output, "Arkanoid Player Progress");
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu file progress: " + e.getMessage());
        }
    }

    public static void resetProgress() {
        try {
            File progressFile = getProgressFile(); // ĐỔI Ở ĐÂY
            if (progressFile.exists()) {
                if (!progressFile.delete()) {
                    System.err.println("Không thể xóa file progress.");
                } else {
                    System.out.println("Đã reset tiến độ.");
                }
            }
        } catch (SecurityException e) {
            System.err.println("Lỗi bảo mật khi xóa file progress: " + e.getMessage());
        }
    }
}
