package life.board;

import java.util.List;
import java.util.Random;

public class Board {
    private final boolean[][] field;
    private static final Random random = new Random();
    private int generation = 0;

    public Board(int dimension) {
        this.field = new boolean[dimension][dimension];
        randomInitField();
    }

    public Board(Board other) {
        this.field = other.field.clone();
        for (int row = 0; row < other.field.length; row++) {
            this.field[row] = other.field[row].clone();
        }
        this.generation = other.generation;
    }

    public int getDimension() {
        return field.length;
    }

    public boolean getCell(int row, int col) {
        return field[row][col];
    }

    public int getAlive() {
        int alive = 0;
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                if (field[row][col]) alive++;
            }
        }
        return alive;
    }

    public int getGeneration() {
        return generation;
    }

    public void print() {
        for (boolean[] row : field) {
            for (Boolean cell : row) {
                System.out.print(cell ? 'O' : ' ');
            }
            System.out.println();
        }
    }

    private void randomInitField() {
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                field[row][col] = random.nextBoolean();
            }
        }
    }

    public Board step() {
        Board newBoard = new Board(this);
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                Pos pos = new Pos(row, col);
                List<Pos> neighbors = pos.getNeighbors(getDimension());
                int countAlive = (int) neighbors.stream().filter(p -> field[p.getRow()][p.getCol()]).count();
                if (field[row][col] && !(countAlive == 2 || countAlive == 3)) {
                    newBoard.field[row][col] = false;
                } else if (!field[row][col] && countAlive == 3) {
                    newBoard.field[row][col] = true;
                }
            }
        }
        newBoard.generation++;
        return newBoard;
    }
}
