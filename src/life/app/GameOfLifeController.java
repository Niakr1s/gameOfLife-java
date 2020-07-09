package life.app;

import java.awt.*;

public class GameOfLifeController {
    private final GameOfLifeModel model;
    private final Thread thread;

    public GameOfLifeController(GameOfLifeModel model) {
        this.model = model;
        thread = new Thread(() -> {
            try {
                while (true) {
                    synchronized (this) {
                        if (model.getStatus().isPaused()) wait();
                    }
                    model.step();
                    Thread.sleep(model.getStatus().getDelayMs());
                }
            } catch (InterruptedException ignored) {
            }
        });
    }

    public void start() {
        thread.start();
    }

    public void setColor(Color color) {
        model.setColor(color);
    }

    public synchronized void pauseResume() {
        model.changeStatus(s -> {
            GameOfLifeModel.Status newStatus = s.setPaused(!s.isPaused());
            if (s.isPaused()) {
                notifyAll();
            }
            return newStatus;
        });
    }

    public void reset(int dimension) throws IllegalArgumentException {
        if (dimension < 5 || dimension > 100) throw new IllegalArgumentException("Dimension out of bounds");
        model.reset(dimension);
    }

    public void setDelayMs(int delayMs) {
        model.changeStatus(s -> s.setDelayMs(delayMs));
    }
}
