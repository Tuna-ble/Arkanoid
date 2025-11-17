package org.example.gamelogic.core;

import javafx.scene.canvas.GraphicsContext;
import org.example.config.GameConstants;
import org.example.gamelogic.entities.Ball;
import org.example.gamelogic.entities.IBall;
import org.example.gamelogic.entities.Paddle;
import org.example.gamelogic.factory.BallFactory;
import org.example.gamelogic.registry.BallRegistry;
import org.example.data.SavedGameState;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;

/**
 * Quản lý các quả bóng trong trò chơi.
 *
 * <p>Chịu trách nhiệm tạo, cập nhật, render và lưu/khôi phục trạng thái các quả bóng.
 * Tài liệu Javadoc ở đây ngắn gọn: mô tả ý nghĩa hàm, các tham số mong đợi và giá trị trả về.
 */
public final class BallManager {
    private final BallFactory ballFactory;
    private List<IBall> activeBalls;

    public BallManager() {
        this.activeBalls =  new ArrayList<>();
        BallRegistry registry = BallRegistry.getInstance();
        registerBallPrototypes(registry);
        this.ballFactory = new BallFactory(registry);
    }

    /**
     * Đăng ký prototype cho các loại bóng vào registry để phục vụ việc tạo đối tượng sau này.
     *
     * @param registry BallRegistry để đăng ký prototype
     */
    private void registerBallPrototypes(BallRegistry registry) {
        registry.register("STANDARD", new Ball(0, 0, GameConstants.BALL_RADIUS));
    }

    /**
     * Tạo quả bóng ban đầu và gắn nó vào vị trí của paddle.
     *
     * @param paddle paddle hiện tại (kỳ vọng không null) — dùng để tính vị trí ban đầu của bóng
     */
    public void createInitialBall(Paddle paddle) {
        activeBalls.clear();
        IBall ball = ballFactory.createBall("STANDARD", 0, 0);
        if (ball != null) {
            ball.reset(paddle.getX(), paddle.getY(), paddle.getWidth());
            activeBalls.add(ball);
        }
    }

    /**
     * Cập nhật trạng thái tất cả quả bóng theo thời gian trôi đi.
     *
     * @param deltaTime thời gian (giây) kể từ lần cập nhật trước — dùng để tính di chuyển
     */
    public void update(double deltaTime) {
        for (IBall ball : activeBalls) {
            ball.update(deltaTime);
        }

        activeBalls.removeIf(IBall::isDestroyed);
    }

    /**
     * Thả các quả bóng đang gắn vào paddle (nếu có) để bắt đầu chuyển động.
     */
    public void releaseAttachedBalls() {
        for (IBall ball : activeBalls) {
            if (ball instanceof Ball && ((Ball) ball).isAttachedToPaddle()) {
                ball.release();
            }
        }
    }

    /**
     * Vẽ tất cả quả bóng lên canvas.
     *
     * @param gc GraphicsContext của canvas (kỳ vọng không null)
     */
    public void render(GraphicsContext gc) {
        for (IBall ball : activeBalls) {
            ball.render(gc);
        }
    }

    /**
     * Lấy danh sách các quả bóng đang hoạt động.
     *
     * @return danh sách tham chiếu (mutable) các IBall hiện có; trả về danh sách rỗng nếu không có
     */
    public List<IBall> getActiveBalls() {
        return activeBalls;
    }

    /**
     * Xóa tất cả bóng hiện có và tạo lại quả bóng khởi tạo gắn với paddle.
     *
     * @param paddle paddle dùng để đặt vị trí bóng mới (kỳ vọng không null)
     */
    public void resetBalls(Paddle paddle) {
        activeBalls.clear();
        createInitialBall(paddle);
    }

    /**
     * Lấy quả bóng chính (đầu danh sách), thường để kiểm tra va chạm chính.
     *
     * @return IBall đầu tiên nếu tồn tại; ngược lại trả về null
     */
    public IBall getPrimaryBall() {
        return activeBalls.isEmpty() ? null : activeBalls.get(0);
    }

    /**
     * Xóa tất cả quả bóng (dọn sạch danh sách).
     */
    public void clear() {
        activeBalls.clear();
    }

    /**
     * Thêm một quả bóng mới vào quản lý nếu hợp lệ.
     *
     * @param ball đối tượng IBall để thêm; nếu null hoặc đang gắn vào paddle thì không thêm
     */
    public void addBall(IBall ball) {
        if (ball != null && !ball.isAttachedToPaddle()) {
            this.activeBalls.add(ball);
        }
    }

    /**
     * Đếm số quả bóng đang ở trạng thái active.
     *
     * @return số lượng bóng active hiện tại (long)
     */
    public long countActiveBalls() {
        return activeBalls.stream().filter(IBall::isActive).count();
    }

    /**
     * Chuẩn bị dữ liệu các quả bóng để lưu trạng thái trò chơi.
     *
     * @return danh sách BallData chứa vị trí và vận tốc của từng quả bóng
     */
    public List<SavedGameState.BallData> getDataToSave() {
        List<SavedGameState.BallData> ballDataList = new ArrayList<>();
        for (IBall ball : activeBalls) {
            ballDataList.add(new SavedGameState.BallData(
                    ball.getX(),
                    ball.getY(),
                    ball.getDx(),
                    ball.getDy()
            ));
        }
        return ballDataList;
    }

    /**
     * Khôi phục trạng thái các quả bóng từ dữ liệu đã lưu.
     *
     * @param ballDataList danh sách BallData đã lưu (vị trí và vận tốc); nếu rỗng sẽ không thêm bóng
     */
    public void loadData(List<SavedGameState.BallData> ballDataList) {
        activeBalls.clear();

        for (SavedGameState.BallData data : ballDataList) {

            IBall ball = ballFactory.createBall("STANDARD", 0, 0);

            if (ball != null) {
                ball.setPosition(data.x, data.y);
                ball.setDx(data.vx);
                ball.setDy(data.vy);

                ball.release();
                activeBalls.add(ball);
            }
        }
    }
}