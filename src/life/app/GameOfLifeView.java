package life.app;

import life.board.Board;
import life.mvc.View;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CancellationException;

public class GameOfLifeView extends JFrame implements View {
    private final Display display;
    private final InfoArea infoArea;
    private GameOfLifeModel model;
    private final ControlArea controlArea;

    public GameOfLifeView() {
        setTitle("Game of life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        this.controlArea = new ControlArea();
        leftPanel.add(controlArea);

        infoArea = new InfoArea();
        leftPanel.add(infoArea);

        mainPanel.add(leftPanel);

        display = new Display();
        mainPanel.add(display);

        add(mainPanel);
        pack();
    }

    public void setModel(GameOfLifeModel model) {
        this.model = model;
    }

    public void setController(GameOfLifeController controller) {
        this.controlArea.setController(controller);
    }

    @Override
    public void update() {
        Board board = model.getBoard();
        GameOfLifeModel.Status status = model.getStatus();
        infoArea.getGenerationLabel().setGeneration(board.getGeneration());
        infoArea.getAliveLabel().setAlive(board.getAlive());
        controlArea.getPauseResumeButton().setPaused(status.isPaused());
        controlArea.getSlider().setValue(status.getDelayMs());
        display.setColor(model.getColor());
        display.setBoard(board);
    }

    private static class InfoArea extends JPanel {
        private final GenerationLabel generationLabel;
        private final AliveLabel aliveLabel;

        public InfoArea() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            generationLabel = new GenerationLabel();
            aliveLabel = new AliveLabel();
            add(generationLabel);
            add(aliveLabel);
        }

        public GenerationLabel getGenerationLabel() {
            return generationLabel;
        }

        public AliveLabel getAliveLabel() {
            return aliveLabel;
        }
    }

    private class ControlArea extends JPanel {
        private final PauseResumeButton pauseResumeButton;
        private final ResetButton resetButton;
        private final SpeedSlider slider;
        private final ColorChooseButton colorChooseButton;

        public ControlArea() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setAlignmentY(0);

            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

            pauseResumeButton = new PauseResumeButton();
            resetButton = new ResetButton();
            slider = new SpeedSlider();
            colorChooseButton = new ColorChooseButton();

            buttonsPanel.add(pauseResumeButton);
            buttonsPanel.add(resetButton);
            add(buttonsPanel);
            add(slider);
            add(colorChooseButton);

            pack();
        }

        public void setController(GameOfLifeController controller) {
            pauseResumeButton.setController(controller);
            resetButton.setController(controller);
            colorChooseButton.setController(controller);
            slider.setController(controller);
        }

        public PauseResumeButton getPauseResumeButton() {
            return pauseResumeButton;
        }

        public ResetButton getResetButton() {
            return resetButton;
        }

        public ColorChooseButton getColorChooseButton() {
            return colorChooseButton;
        }

        public SpeedSlider getSlider() {
            return slider;
        }

    }
}

class SpeedSlider extends JSlider {
    public SpeedSlider() {
        super();
        setMinimum(20);
        setMaximum(2000);
    }

    public void setController(GameOfLifeController controller) {
        addChangeListener(e -> {
            controller.setDelayMs(getValue());
        });
    }
}

class ResetButton extends JButton {
    public ResetButton() {
        setName("ResetButton");
        setText("reset");
    }

    public void setController(GameOfLifeController controller) {
        addActionListener(event -> {
            String error = "";
            while (true) {
                try {
                    controller.reset(getDimension(error));
                    return;
                } catch (IllegalArgumentException e) {
                    error = e.toString();
                } catch (CancellationException ignored) {
                    return;
                }
            }
        });
    }

    private int getDimension(String error) throws CancellationException {
        String res = JOptionPane.showInputDialog("Input new board size.\n" + error);
        if (res == null || res.equals("")) throw new CancellationException();
        return Integer.parseInt(res);
    }
}

class PauseResumeButton extends JToggleButton {
    public PauseResumeButton() {
        setName("PlayToggleButton");
    }

    public void setController(GameOfLifeController controller) {
        addActionListener(e -> controller.pauseResume());
    }

    public void setPaused(boolean paused) {
        if (paused) {
            setText("resume");
        } else {
            setText("pause");
        }
    }
}

class ColorChooseButton extends JButton {
    private GameOfLifeController controller;

    public ColorChooseButton() {
        setText("Color");
    }

    public void setController(GameOfLifeController controller) {
        this.controller = controller;
        addActionListener(e -> {
            getColor();
        });
    }

    private void getColor() {
        JColorChooser colorChooser = new JColorChooser();
        JDialog dialog = JColorChooser.createDialog(this, "Choose color", true, colorChooser, e -> {
            controller.setColor(colorChooser.getColor());
        }, null);
        dialog.setVisible(true);
    }
}

class GenerationLabel extends JLabel {
    public GenerationLabel() {
        setName("GenerationLabel");
        setGeneration(0);
    }

    public void setGeneration(int generation) {
        setText("Generation #" + generation);
    }
}

class AliveLabel extends JLabel {
    public AliveLabel() {
        setName("AliveLabel");
        setAlive(0);
    }

    public void setAlive(int alive) {
        setText("Alive: " + alive);
    }
}

class Display extends JPanel {
    private Board board;
    private Color color = Color.BLACK;

    public Display() {
        setPreferredSize(new Dimension(600, 600));
    }

    public void setBoard(Board board) {
        synchronized (this) {
            this.board = board;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (this) {
            paintGrid(g);
            paintBoard(g);
        }
    }

    public synchronized void setColor(Color color) {
        this.color = color;
    }

    private void paintBoard(Graphics g) {
        if (board == null) return;

        int dim = board.getDimension();
        int cellSize = getCellSize(dim);
        setPreferredSize(new Dimension(dim * cellSize, dim * cellSize));
        g.setColor(this.color);
        for (int row = 0; row < dim; row++) {
            for (int col = 0; col < dim; col++) {
                if (board.getCell(row, col)) {
                    g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }
            }
        }
        updateSize(dim);
    }

    private void paintGrid(Graphics g) {
        if (board == null) return;

        int dim = board.getDimension();
        int cellSize = getCellSize(dim);
        for (int col = 0; col <= dim; col++) {
            int x = col * cellSize;
            if (col == dim) x--;
            g.drawLine(x, 0, x, getHeight());
        }
        for (int row = 0; row <= dim; row++) {
            int y = row * cellSize;
            if (row == dim) y--;
            g.drawLine(0, y, getWidth(), y);
        }
        updateSize(dim);
    }

    private int getCellSize(int dimension) {
        return Math.min(getWidth(), getHeight()) / dimension;
    }

    private void updateSize(int dimension) {
        int cellSize = getCellSize(dimension);
        int sz = cellSize * dimension;
        setPreferredSize(new Dimension(sz, sz));
    }
}
