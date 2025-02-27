package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class MainMenuView extends JFrame {
    private JButton newGameButton;
    private JButton highScoresButton;
    private JButton exitButton;

    public MainMenuView() {
        super("Coronavirus AntiPlague - Main Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new GridLayout(3,1));

        newGameButton = new JButton("New Game");
        highScoresButton = new JButton("High Scores");
        exitButton = new JButton("Exit");

        add(newGameButton);
        add(highScoresButton);
        add(exitButton);

        initListeners();
    }

    private void initListeners() {
        newGameButton.addActionListener(e -> {
            GameController controller = new GameController();

            String[] diffs = { "Easy", "Medium", "Hard" };
            String diff = (String) JOptionPane.showInputDialog(this,
                    "Choose Difficulty:",
                    "Difficulty",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    diffs,
                    diffs[0]);
            if (diff != null) {
                controller.startNewGame(diff);
                dispose();
            }
        });

        highScoresButton.addActionListener(e -> {

            GameController tempController = new GameController();
            tempController.showRanking(this);
        });

        exitButton.addActionListener(e -> System.exit(0));
    }
}
