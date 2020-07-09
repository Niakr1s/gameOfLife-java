package life.app;

import life.board.Board;
import life.mvc.AbstractModel;

import java.awt.*;
import java.util.function.UnaryOperator;

public class GameOfLifeModel extends AbstractModel {
    private Board board;
    private Status status;
    private Color color = Color.BLACK;

    public GameOfLifeModel(int dimension) {
        this.board = new Board(dimension);
        this.status = new Status();
    }

    public synchronized void reset(int dimension) {
        this.board = new Board(dimension);
        modelUpdated();
    }

    public synchronized void step() {
        board = board.step();
        modelUpdated();
    }

    public synchronized void setColor(Color color) {
        this.color = color;
    }

    public synchronized Color getColor() {
        return color;
    }

    public synchronized Board getBoard() {
        return board;
    }

    public synchronized Status getStatus() {
        return this.status;
    }

    public synchronized  void changeStatus(UnaryOperator<Status> supplier) {
        this.status = supplier.apply(this.status);
        modelUpdated();
    }

    public static class Status {
        private int delayMs;
        private boolean paused;

        public Status() {
            this.delayMs = 500;
            this.paused = false;
        }

        public Status(Status other) {
            this.delayMs = other.delayMs;
            this.paused = other.paused;
        }

        public int getDelayMs() {
            return delayMs;
        }

        public Status setDelayMs(int delayMs) {
            Status res = new Status(this);
            res.delayMs = delayMs;
            return res;
        }

        public boolean isPaused() {
            return paused;
        }

        public Status setPaused(boolean paused) {
            Status res = new Status(this);
            res.paused = paused;
            return res;
        }
    }
}
