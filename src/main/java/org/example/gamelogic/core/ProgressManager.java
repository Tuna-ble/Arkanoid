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

public final class ProgressManager {

    private static final String PROGRESS_FILE_PATH =
            System.getProperty("user.home") + File.separator + ".arkanoid_progress.properties";
    private static final int TOTAL_LEVELS = 5;

    public static Map<Integer, Integer> loadStars() {
        Map<Integer, Integer> starsMap = new HashMap<>();
        Properties props = new Properties();
        File progressFile = new File(PROGRESS_FILE_PATH);

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
        File progressFile = new File(PROGRESS_FILE_PATH);
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
        if (starsAwarded < 1) return;

        Properties props = new Properties();
        File progressFile = new File(PROGRESS_FILE_PATH);

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
            File progressFile = new File(PROGRESS_FILE_PATH);
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