package life;

import life.app.GameOfLifeController;
import life.app.GameOfLifeModel;
import life.app.GameOfLifeView;
import life.board.Board;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        GameOfLifeModel m = new GameOfLifeModel(40);
        GameOfLifeController c = new GameOfLifeController(m);
        GameOfLifeView v = new GameOfLifeView();
        v.setModel(m);
        v.setController(c);
        m.setView(v);
        c.start();
    }

    private static void runConsole() throws InterruptedException {
        Scanner in = new Scanner(System.in);
        int dim = in.nextInt();
        Board board = new Board(dim);
        clearConsole();
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100);
            board = board.step();
            clearConsole();
            System.out.println("Generation #" + (i + 1));
            System.out.println("Alive: " + board.getAlive());
            board.print();
        }
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ignored) {
        }
    }
}
