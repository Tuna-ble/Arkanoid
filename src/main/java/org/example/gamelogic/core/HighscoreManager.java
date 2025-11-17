package org.example.gamelogic.core;

import org.example.gamelogic.multithreading.AsyncExecutor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Hỗ trợ lưu/đọc highscore cục bộ của người chơi (ghi vào file trong home directory).
 *
 * <p>Phương thức tĩnh tiện dụng: loadHighscores, saveNewScore, resetHighscores.
 */
public final class HighscoreManager {

    private static final String HIGHSCORE_FILE_PATH =
            System.getProperty("user.home") + File.separator + ".arkanoid_scores.txt";

    private static final int MAX_SCORES_TO_KEEP = 3;

    /**
     * Đọc file highscore và trả về top N điểm (giới hạn bởi MAX_SCORES_TO_KEEP).
     *
     * @return danh sách điểm giảm dần (có thể rỗng)
     */
    public static List<Integer> loadHighscores() {
        List<Integer> scores = new ArrayList<>();
        File scoreFile = new File(HIGHSCORE_FILE_PATH);

        if (!scoreFile.exists()) {
            try {
                scoreFile.createNewFile();
                return scores;
            } catch (IOException e) {
                System.err.println("Không thể tạo file highscore: " + e.getMessage());
                return scores;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    scores.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
//
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file highscore: " + e.getMessage());
        }

        Collections.sort(scores, Collections.reverseOrder());
        return scores.subList(0, Math.min(scores.size(), MAX_SCORES_TO_KEEP));
    }

    /**
     * Lưu điểm mới một cách bất đồng bộ (ghi đè file với top N điểm).
     *
     * @param newScore điểm cần lưu (>=0)
     */
    public static void saveNewScore(int newScore) {
        AsyncExecutor.runAsync(() -> {
            List<Integer> scores = loadHighscores();

            scores.add(newScore);
            Collections.sort(scores, Collections.reverseOrder());
            List<Integer> topScores = scores.subList(0, Math.min(scores.size(), MAX_SCORES_TO_KEEP));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE_PATH, false))) {
                for (Integer score : topScores) {
                    writer.write(score.toString());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Lỗi khi lưu file highscore: " + e.getMessage());
            }
        });
    }

    /**
     * Xóa file highscore (nếu tồn tại).
     */
    public static void resetHighscores() {
        try {
            File scoreFile = new File(HIGHSCORE_FILE_PATH);
            if (scoreFile.exists()) {
                if (!scoreFile.delete()) {
                    System.err.println("Không thể xóa file highscore.");
                } else {
                    System.out.println("Đã reset highscore.");
                }
            }
        } catch (SecurityException e) {
            System.err.println("Lỗi bảo mật khi xóa file highscore: " + e.getMessage());
        }
    }
}