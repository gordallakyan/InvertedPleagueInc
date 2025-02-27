package view;

import javax.swing.*;
import java.awt.*;
import controller.GameController;

public class DifficultyDialog extends JDialog {
    private JComboBox<String> difficultyCombo;
    private JButton startButton;
    private GameController controller;

    public DifficultyDialog(JFrame parent, GameController controller) {
        super(parent, "Choose Difficulty", true);
        this.controller = controller;
        setSize(300, 150);
        setLayout(new BorderLayout());

        difficultyCombo = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        startButton = new JButton("Start Game");

        add(difficultyCombo, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(e -> {
            String diff = (String)difficultyCombo.getSelectedItem();
            controller.startNewGame(diff);
            dispose();
        });
    }
}
