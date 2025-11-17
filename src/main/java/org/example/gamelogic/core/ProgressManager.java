package org.example.gamelogic.core;

import org.example.gamelogic.multithreading.AsyncExecutor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ProgressManager {
    /**
     * Quản lý tiến độ và session của người chơi (lưu cục bộ vào file properties trong home directory).
     *
     * <p>Các phương thức tĩnh để đọc/ghi stars, session và reset progress.
     */

    private static final String SESSION_FILE_PATH =
            System.getProperty("user.home") + File.separator + ".arkanoid_session.properties";
    private static final String PROGRESS_FILE_PATH =
            System.getProperty("user.home") + File.separator + ".arkanoid_progress.properties";
    private static final int TOTAL_LEVELS = 5;

    /**
     * Đọc số sao đã đạt cho từng level từ file progress.
     *
     * @return map level->stars (chỉ chứa các level có stars > 0)
     */
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

    /**
     * Lấy mức level tối đa đã mở từ file progress.
     *
     * @return level tối đa đã mở (ít nhất 1)
     */
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

    /**
     * Lưu tiến độ bất đồng bộ: cập nhật số sao và mở level tiếp theo nếu cần.
     *
     * @param levelCompleted level vừa hoàn thành
     * @param starsAwarded số sao được trao (nên >= 1)
     */
    public static void saveProgress(int levelCompleted, int starsAwarded) {
        AsyncExecutor.runAsync(() -> {
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
        });
    }

    /**
     * Xóa file progress và session (reset tiến độ chơi).
     */
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
        try {
            File sessionFile = new File(SESSION_FILE_PATH);
            if (sessionFile.exists()) {
                if (!sessionFile.delete()) {
                    System.err.println("Không thể xóa file session.");
                } else {
                    System.out.println("Đã reset tiến độ.");
                }
            }
        } catch (SecurityException e) {
            System.err.println("Lỗi bảo mật khi xóa file progress: " + e.getMessage());
        }
    }

    /**
     * Lưu session hiện tại (bất đồng bộ) cho một chế độ chơi cụ thể.
     *
     * @param gameMode tên chế độ chơi (ví dụ "LEVEL" hoặc "INFINITE")
     * @param currentScore điểm hiện tại
     * @param elapsedTime thời gian đã chơi (giây)
     * @param levelNumber số level hiện tại
     * @param currentLives số mạng hiện tại
     */
    public static void saveSession(String gameMode, int currentScore, double elapsedTime, int levelNumber, int currentLives) {
        AsyncExecutor.runAsync(() -> {
            File sessionFile = new File(SESSION_FILE_PATH);
            Properties props = new Properties();

            if (sessionFile.exists()) {
                try (InputStream input = new FileInputStream(sessionFile)) {
                    props.load(input);
                } catch (IOException e) {
                    System.err.println("Không thể tải session cũ: " + e.getMessage());
                }
            }

            String prefix = gameMode.toLowerCase();

            props.setProperty(prefix + ".score", String.valueOf(currentScore));
            props.setProperty(prefix + ".time", String.valueOf(elapsedTime));
            props.setProperty(prefix + ".level", String.valueOf(levelNumber));
            props.setProperty(prefix + ".lives", String.valueOf(currentLives));

            try (OutputStream output = new FileOutputStream(sessionFile)) {
                props.store(output, "Arkanoid " + gameMode + " Session Save");
            } catch (IOException e) {
                System.err.println("Lỗi khi lưu file " + gameMode + " session: " + e.getMessage());
            }
        });
    }

    /**
     * Đọc session đã lưu cho chế độ chơi cụ thể.
     *
     * @param gameMode tên chế độ chơi
     * @return map chứa các giá trị session (score, time, level, lives) dưới dạng string; rỗng nếu không có
     */
    public static Map<String, String> loadSession(String gameMode) {
        Map<String, String> data = new HashMap<>();
        File sessionFile = new File(SESSION_FILE_PATH);

        if (!sessionFile.exists()) {
            return data;
        }

        Properties props = new Properties();
        try (InputStream input = new FileInputStream(sessionFile)) {
            props.load(input);

            String prefix = gameMode.toLowerCase();

            data.put("score", props.getProperty(prefix + ".score", "0"));
            data.put("time", props.getProperty(prefix + ".time", "0"));
            data.put("level", props.getProperty(prefix + ".level", "1"));
            data.put("lives", props.getProperty(prefix + ".lives", "3"));
        } catch (IOException | NumberFormatException e) {
            System.err.println("Lỗi khi đọc file session: " + e.getMessage());
        }
        return data;
    }

    /**
     * Xóa session đã lưu cho chế độ chơi chỉ định (bất đồng bộ).
     *
     * @param gameMode tên chế độ chơi
     */
    public static void clearSession(String gameMode) {
        AsyncExecutor.runAsync(() -> {
            File sessionFile = new File(SESSION_FILE_PATH);
            if (!sessionFile.exists()) {
                return;
            }

            Properties props = new Properties();
            try (InputStream input = new FileInputStream(sessionFile)) {
                props.load(input);
            } catch (IOException e) {
                System.err.println("Lỗi khi đọc file session: " + e.getMessage());
            }

            String prefix = gameMode.toLowerCase();

            props.remove(prefix + ".score");
            props.remove(prefix + ".time");
            props.remove(prefix + ".level");
            props.remove(prefix + ".lives");

            try (OutputStream output = new FileOutputStream(sessionFile)) {
                props.store(output, "Arkanoid Sessions");
            } catch (IOException e) {
                System.err.println("Lỗi khi lưu file session: " + e.getMessage());
            }
        });
    }
}
