package life.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pos {
    private final int row;
    private final int col;

    public Pos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Pos(Pos other) {
        this.row = other.row;
        this.col = other.col;
    }

    public Pos sum(Pos other) {
        return new Pos(row + other.row, col + other.col);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public List<Pos> getNeighbors(int dimension) {
        List<Pos> neighbors = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;
                Pos diff = new Pos(x, y);
                neighbors.add(sum(diff));
            }
        }
        for (int i = 0; i < neighbors.size(); i++) {
            neighbors.set(i, neighbors.get(i).normalized(dimension));
        }
        return neighbors;
    }

    /**
     * Produces position, fitted to dimension, ie: for Pos{-1,-1} and dim=5 it should return Pos{4,4}
     */
    public Pos normalized(int dimension) {
        int row = normalizeAxis(this.row, dimension);
        int col = normalizeAxis(this.col, dimension);
        return new Pos(row, col);
    }

    private static int normalizeAxis(int axis, int dimension) {
        while (axis < 0) {
            axis += dimension;
        }
        axis %= dimension;
        return axis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pos pos = (Pos) o;
        return row == pos.row &&
                col == pos.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
