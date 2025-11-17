package org.example.data;

import java.util.List;

public final class LevelData {
    private final List<String> layout;

    /**
     * Tạo đối tượng chứa dữ liệu layout màn chơi.
     *
     * @param layout danh sách các dòng mô tả cấu trúc màn chơi
     */
    public LevelData(List<String> layout) {
        this.layout = layout;
    }

    /**
     * Lấy danh sách các dòng layout của màn chơi.
     *
     * @return danh sách chuỗi mô tả bố cục màn chơi
     */
    public List<String> getLayout() {
        return this.layout;
    }
}