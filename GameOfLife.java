import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;

public class GameOfLife extends JFrame {

    JPanel infoPanel;
    static JLabel generationLabel;
    static JLabel aliveLabel;
    static JPanel universePanel;

    static boolean paused = false;

    static Universe universe = new Universe();
    static Evolution evolution = new Evolution();

    public GameOfLife() {

        super("Game of Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 789);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);

        infoPanel = new JPanel();
        infoPanel.setBounds(1, 1, 110, 40);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        add(infoPanel);

        generationLabel = new JLabel();
        generationLabel.setName("GenerationLabel");
        generationLabel.setText(" Generation #0");
        generationLabel.setBounds(0, 0, 400, 20);
        infoPanel.add(generationLabel);

        aliveLabel = new JLabel();
        aliveLabel.setName("AliveLabel");
        aliveLabel.setText(" Alive: " + GameOfLife.universe.alive);
        aliveLabel.setBounds(0, 20, 400, 20);
        infoPanel.add(aliveLabel);

        JPanel controlPanel = new JPanel();
        controlPanel.setBounds(111, 1, 121, 40);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setBorder(BorderFactory.createEtchedBorder());
        add(controlPanel);

        JToggleButton pauseButton = new JToggleButton("||");
        pauseButton.setName("PlayToggleButton");
        ActionListener pauseListener = new PauseListener();
        pauseButton.addActionListener(pauseListener);
        controlPanel.add(pauseButton);

        JButton restartButton = new JButton("Restart");
        restartButton.setName("ResetButton");
        ActionListener restartListener = new RestartListener();
        restartButton.addActionListener(restartListener);
        controlPanel.add(restartButton);

        universePanel = new UniversePanel();
        add(universePanel);
    }

    public static void main(String[] args) {

        new GameOfLife();
        universe.setSize(35);
        evolution.setGenerations(500);

        while (true) {

            if (!evolution.restart) {

                universe.create();
                evolution.evolve(universe);

            }
        }
    }
}

class Universe {

    int size;
    static int genNum;
    static int alive;
    char[][] field;
    Random rand;

    public Universe() {}

    public int getSize() {

        return this.size;

    }

    public void setSize(int size) {

        if (size > 0) {

            this.size = size;

        } else {

            System.out.println("Size must be greater than 0.");

        }

    }

    public void create() {

        this.genNum = 0;
        this.alive = 0;
        this.rand = new Random();

        this.field = new char[size][size];

        for (int i = 0; i < this.size; i++) {

            for (int j = 0; j < this.size; j++) {

                if (this.rand.nextBoolean()) {

                    this.field[i][j] = 'O';
                    alive++;

                } else {

                    this.field[i][j] = ' ';

                }
            }
        }
    }

    public void print() {

        GameOfLife.aliveLabel.setText(" Alive: " + GameOfLife.universe.alive);
        System.out.println("Alive: " + this.alive);

        for (int i = 0; i < this.size; i++) {

            for (int j = 0; j < this.size; j++) {

                System.out.print(j == size - 1 ? this.field[i][j] + "\n" : this.field[i][j]);

            }
        }
    }
}

class Evolution {

    int generations;
    char[][] field;
    boolean restart = false;

    public Evolution() {}

    public void evolve(Universe universe) {

        this.field = new char[universe.size][universe.size];

        if (this.generations == 0) {

            System.out.println("Generation 0");
            GameOfLife.generationLabel.setText(" Generation 0");

            universe.print();
            return;

        }

        for (int g = 1; g <= this.generations; g++) {

            if (restart) {

                restart = false;
                break;

            }

            try {

                while (GameOfLife.paused) {

                    Thread.sleep(1000l);

                }

                Thread.sleep(50l);
                if (g < this.generations) {

                    new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();

                }
            } catch (Exception e) {

                e.printStackTrace();

            }

            universe.alive = 0;

            for (int i = 0; i < universe.getSize(); i++) {

                for (int j = 0; j < universe.getSize(); j++) {

                    int neighbours = 0;
                    int n = i - 1;
                    int s = i + 1;
                    int w = j - 1;
                    int e = j + 1;

                    if (i == 0) {

                        n = universe.getSize() - 1;

                    } else if (i == universe.getSize() - 1) {

                        s = 0;

                    }

                    if (j == 0) {

                        w = universe.getSize() - 1;

                    } else if (j == universe.getSize() - 1) {

                        e = 0;

                    }

                    neighbours += universe.field[n][w] == 'O' ? 1 : 0;
                    neighbours += universe.field[n][j] == 'O' ? 1 : 0;
                    neighbours += universe.field[n][e] == 'O' ? 1 : 0;
                    neighbours += universe.field[i][w] == 'O' ? 1 : 0;
                    neighbours += universe.field[i][e] == 'O' ? 1 : 0;
                    neighbours += universe.field[s][w] == 'O' ? 1 : 0;
                    neighbours += universe.field[s][j] == 'O' ? 1 : 0;
                    neighbours += universe.field[s][e] == 'O' ? 1 : 0;

                    if (universe.field[i][j] == 'O') {

                        this.field[i][j] = neighbours < 2 || neighbours > 3 ? ' ' : 'O';

                    } else {

                        this.field[i][j] = neighbours == 3 ? 'O' : ' ';

                    }
                }
            }

            for (int i = 0; i < universe.getSize(); i++) {

                for (int j = 0; j < universe.getSize(); j++) {

                    universe.field[i][j] = this.field[i][j];

                    if (this.field[i][j] == 'O') {

                        universe.alive++;

                    }
                }
            }

            universe.genNum = g;
            GameOfLife.generationLabel.setText(" Generation #" + universe.genNum);
            System.out.println("Generation #" + g);
            universe.print();
            GameOfLife.universePanel.repaint();
        }
    }

    public void setGenerations(int generations) {

        if (generations >= 0) {

            this.generations = generations;

        } else {

            System.out.println("Number of generations can't be negative.");

        }
    }
}

class UniversePanel extends JPanel {

    public UniversePanel() {

        setBorder(BorderFactory.createLineBorder(Color.black));
        setBounds(1, 41, 700, 700);

    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        for (int i = 1; i < GameOfLife.universe.getSize(); i++) {

            g.drawLine(20 * i, 0, 20 * i, 900);
            g.drawLine(0, 20 * i, 905, 20 * i);

        }

        for (int i = 0; i < GameOfLife.universe.getSize(); i++) {

            for (int j = 0; j < GameOfLife.universe.getSize(); j++) {

                if (GameOfLife.universe.field[i][j] == 'O') {

                    g.fillRect(j * 20, i * 20, 20, 20);

                }
            }
        }
    }
}

class PauseListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

        GameOfLife.paused = !GameOfLife.paused;

    }
}

class RestartListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {

        GameOfLife.evolution.restart = true;

    }
}
