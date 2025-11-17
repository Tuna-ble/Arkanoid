package org.example.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FileLevelRepository implements ILevelRepository {

    /**
     * Tải dữ liệu màn chơi từ file văn bản theo số level.
     *
     * @param levelNumber số màn cần tải (ví dụ: 1, 2, 3...)
     * @return {@link LevelData} chứa danh sách các dòng layout; trả về rỗng nếu file không tồn tại hoặc lỗi I/O
     */
    @Override
    public LevelData loadLevel(int levelNumber) {
        String fileName = "/levels/level_" + levelNumber + ".txt";
        List<String> layout = new ArrayList<>();

        try (var inputStream = this.getClass().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                System.err.println("Không tìm thấy file màn chơi: " + fileName);
                return new LevelData(new ArrayList<>());
            }

            try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                // Đọc tất cả các dòng không rỗng từ file
                layout = reader.lines()
                        .filter(line -> !line.trim().isEmpty())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải màn chơi: " + fileName);
            e.printStackTrace();
        }

        return new LevelData(layout);
    }
}