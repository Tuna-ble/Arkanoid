package org.example.gamelogic.core;

import org.example.data.ILevelRepository;
import org.example.data.LevelData;
import org.example.gamelogic.entities.bricks.*;
import org.example.gamelogic.events.BrickDamagedEvent;
import org.example.gamelogic.events.ExplosiveBrickEvent;
import org.example.gamelogic.events.BallHitBrickEvent;
import org.example.gamelogic.factory.BrickFactory;
import org.example.gamelogic.registry.BrickRegistry;
import org.example.config.GameConstants;
import org.example.data.SavedGameState;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Quản lý các viên gạch trong level: tạo từ layout, cập nhật, vẽ và xử lý lưu/trở lại trạng thái.
 *
 * <p>Các phương thức công khai cho phép tải level, lấy danh sách gạch, kiểm tra hoàn thành
 * và chuẩn bị dữ liệu để lưu trò chơi.
 */
public final class BrickManager {
    private ILevelRepository levelRepository;
    private final BrickFactory brickFactory;
    private List<Brick> bricks;

    public BrickManager(ILevelRepository levelRepository) {
        this.bricks = new ArrayList<>();
        this.levelRepository = levelRepository;
        BrickRegistry registry = BrickRegistry.getInstance();
        registerBrickPrototypes(registry);
        this.brickFactory = new BrickFactory(registry);

        subscribeToExplosiveBrickEvent();
    }

    /**
     * Tạo mới BrickManager với repository cung cấp layout các level.
     *
     * @param levelRepository implementation của ILevelRepository để tải layout
     */

    /**
     * Đăng ký lắng nghe sự kiện ExplosiveBrickEvent để xử lý khi gạch nổ gây sát thương lan.
     */
    private void subscribeToExplosiveBrickEvent() {
        EventManager.getInstance().subscribe(
                ExplosiveBrickEvent.class,
                this::onBrickExploded
        );
    }

    /**
     * Xử lý khi một ExplosiveBrick phát nổ: kiểm tra các gạch khác trong vùng ảnh hưởng
     * và phát sự kiện BrickDamagedEvent cho từng gạch nằm trong phạm vi.
     *
     * @param event sự kiện chứa tham chiếu đến explosive brick
     */
    private void onBrickExploded(ExplosiveBrickEvent event) {
        Iterator<Brick> iterator = bricks.iterator();
        while(iterator.hasNext()) {
            Brick other=iterator.next();
            if (event.getExplosiveBrick().withinRangeOf(other)) {
                EventManager.getInstance().
                        publish(new BrickDamagedEvent(other, event.getExplosiveBrick().getGameObject()));
            }
        }
    }

    /**
     * Đăng ký các prototype của các loại gạch vào registry để sau này tạo nhanh bằng factory.
     *
     * @param brickRegistry registry dùng để đăng ký prototype
     */
    private void registerBrickPrototypes(BrickRegistry brickRegistry) {
        brickRegistry.register("H", new HardBrick(0,  0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        brickRegistry.register("N", new NormalBrick(0,  0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        brickRegistry.register("U", new UnbreakableBrick(0, 0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        brickRegistry.register("E", new ExplosiveBrick(0,  0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        brickRegistry.register("R", new HealingBrick(0, 0,
                GameConstants.BRICK_WIDTH, GameConstants.BRICK_HEIGHT));
        /// HNUE :)))
        /// bựa
    }

    /**
     * Cập nhật trạng thái tất cả gạch.
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước
     */
    public void update(double deltaTime) {
        for (Brick brick : bricks) {
            brick.update(deltaTime);
        }
        bricks.removeIf(Brick::isDestroyed);
    }

    /**
     * Vẽ tất cả gạch lên canvas.
     *
     * @param gc GraphicsContext của canvas (kỳ vọng không null)
     */
    public void render(GraphicsContext gc) {
        for (Brick brick : bricks) {
            brick.render(gc);
        }
    }

    /**
     * Vẽ gạch theo hiệu ứng xuất hiện dần dựa trên timer và duration.
     *
     * @param gc GraphicsContext (kỳ vọng không null)
     * @param timer thời gian hiện tại của hiệu ứng
     * @param duration tổng thời gian của hiệu ứng
     */
    public void render(GraphicsContext gc, double timer, double duration) {
        if (bricks.isEmpty()) {
            return;
        }

        double timePerBrick = duration / bricks.size();

        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            double brickStartTime = i * timePerBrick;

            if (timer < brickStartTime) {
                break;
            }

            double timeSinceSpawn = timer - brickStartTime;
            double alpha = Math.min(1.0, timeSinceSpawn / timePerBrick);

            gc.save();
            try {
                gc.setGlobalAlpha(alpha);
                brick.render(gc);
            } finally {
                gc.restore();
            }
        }
    }

    /**
     * Tải layout level từ repository và tạo các Brick tương ứng.
     *
     * @param levelNumber số level cần tải (1-based)
     */
    public void loadLevel(int levelNumber) {
        this.bricks.clear();
        LevelData levelData = levelRepository.loadLevel(levelNumber);

        List<String> layout = levelData.getLayout();

        for (int row = 0; row < layout.size(); row++) {

            String[] types = layout.get(row).trim().split("\\s+");
            int numCols = 0;
            for (String type : types) {
                if (!type.equals("_")) {
                    numCols++;
                }
            }
            if (numCols == 0) continue;
            double rowWidth = numCols * (GameConstants.BRICK_WIDTH + GameConstants.PADDING) - GameConstants.PADDING;
            double rowStartX = (GameConstants.PLAY_AREA_WIDTH - rowWidth) / 2.0;

            int currentCol = 0;
            for (int col = 0; col < types.length; col++) {
                String type = types[col];
                if (type.equals("_")) {
                    continue;
                }

                double x = GameConstants.PLAY_AREA_X + rowStartX + currentCol * (GameConstants.BRICK_WIDTH + GameConstants.PADDING);
                double y = GameConstants.PLAY_AREA_Y + GameConstants.TOP_MARGIN + row * (GameConstants.BRICK_HEIGHT + GameConstants.PADDING);

                Brick brick = brickFactory.createBrick(type, x, y);
                if (brick != null) {

                    int brickId = (row * 100) + currentCol;
                    brick.setId(brickId);
                    this.bricks.add(brick);
                }
                currentCol++;
            }
        }
    }

    /**
     * Kiểm tra xem tất cả gạch có thể phá có bị phá hết không.
     *
     * @return true nếu không còn gạch có thể phá; false ngược lại
     */
    public boolean isLevelComplete() {
        for (Brick brick : bricks) {
            if (brick.isBreakable()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Đặt repository để tải dữ liệu level.
     *
     * @param repo implementation của ILevelRepository (kỳ vọng không null)
     */
    public void setLevelRepository(ILevelRepository repo) {
        this.levelRepository = repo;
    }

    /**
     * Lấy danh sách gạch hiện tại.
     *
     * @return danh sách tham chiếu các Brick (mutable)
     */
    public List<Brick> getBricks() {
        return bricks;
    }

    /**
     * Chuẩn bị dữ liệu các gạch để lưu trạng thái trò chơi.
     *
     * @return danh sách BallData (id, health, destroyed) để ghi vào SavedGameState
     */
    public List<SavedGameState.BrickData> getDataToSave() {
        List<SavedGameState.BrickData> brickDataList = new ArrayList<>();
        for (Brick brick : bricks) {
            brickDataList.add(new SavedGameState.BrickData(
                    brick.getId(),
                    brick.getHealth(),
                    brick.isDestroyed()
            ));
        }
        return brickDataList;
    }

    /**
     * Áp dụng dữ liệu gạch đã lưu lên layout hiện tại (từ loadLevel).
     *
     * @param savedBrickDataList danh sách dữ liệu gạch đã lưu (id, health, destroyed)
     */
    public void loadData(List<SavedGameState.BrickData> savedBrickDataList) {

        Set<Integer> savedBrickIds = new HashSet<>();
        Map<Integer, Integer> savedBrickHealth = new HashMap<>();

        for (SavedGameState.BrickData data : savedBrickDataList) {
            savedBrickIds.add(data.id);
            savedBrickHealth.put(data.id, data.health);
        }

        System.out.println("--- DEBUG (LOGIC MỚI): Bắt đầu tải gạch ---");
        System.out.println("Số gạch CÒN SỐNG đã lưu: " + savedBrickIds.size());
        System.out.println("Tổng số gạch MỚI (từ loadLevel): " + this.bricks.size());

        Iterator<Brick> iterator = this.bricks.iterator();
        int destroyedCount = 0;

        while (iterator.hasNext()) {
            Brick newBrick = iterator.next();
            int newBrickId = newBrick.getId();

            if (savedBrickIds.contains(newBrickId)) {

                int savedHealth = savedBrickHealth.get(newBrickId);
                newBrick.setHealth(savedHealth);
            } else {

                newBrick.setDestroyed(true);
                destroyedCount++;
            }
        }

        System.out.println("Số gạch đã bị phá hủy (được tải): " + destroyedCount);
        System.out.println("--- DEBUG (LOGIC MỚI): Kết thúc tải gạch ---");

        bricks.removeIf(Brick::isDestroyed);
    }
}