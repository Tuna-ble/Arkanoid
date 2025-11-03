package org.example.data;

import java.util.List;

public final class LevelData {
    private final List<String> layout;

    public LevelData(List<String> layout) {
        this.layout = layout;
    }

    public List<String> getLayout() {
        return this.layout;
    }
}