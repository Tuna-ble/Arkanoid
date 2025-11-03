package org.example.data;

import org.example.gamelogic.factory.BrickFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class InfiniteLevelRepository implements ILevelRepository {
    private final Random random=new Random();
    private final int rows=5;
    private final int cols=10;

    @Override
    public LevelData loadLevel(int levelNumber) {
        List<String> layout=new ArrayList<>();
        for (int row=0; row<rows; row++) {
            String current="";
            for (int col=0; col<cols; col++) {
                if (random.nextDouble()<0.75) {
                    int type=random.nextInt(100);
                    if (type<30) {
                        current+="H ";
                    }
                    else if (type>85 && type<=95) {
                        current+="E ";
                    }
                    else if (type>95) {
                        current+="R ";
                    }
                    else {
                        current+="N ";
                    }
                }
                else {
                    current+="_ ";
                }
            }
            layout.add(current);
        }
        return new LevelData(layout);
    }
}
