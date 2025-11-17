package org.example.data;

public interface ILevelRepository {
    /**
     * Tải dữ liệu layout của một màn chơi dựa trên số level.
     *
     * @param levelNumber số màn cần tải
     * @return {@link LevelData} chứa nội dung màn chơi; có thể rỗng nếu không tìm thấy hoặc lỗi
     */
    LevelData loadLevel(int levelNumber);
}
