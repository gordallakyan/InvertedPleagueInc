package view;

import model.Upgrade;
import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UpgradesDialog extends JDialog {
    private GameController controller;
    private JPanel upgradesPanel;

    public UpgradesDialog(JFrame parent, GameController controller) {
        super(parent, "Upgrades", true);
        this.controller = controller;

        setSize(400, 300);
        setLayout(new BorderLayout());

        upgradesPanel = new JPanel(new GridLayout(0, 1));
        add(new JScrollPane(upgradesPanel), BorderLayout.CENTER);

        loadUpgrades();
    }

    private void loadUpgrades() {
        List<Upgrade> upgrades = controller.getAvailableUpgrades();
        for (Upgrade u : upgrades) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel(u.getName() + " - Cost: " + u.getCost()), BorderLayout.WEST);
            JButton buyButton = new JButton("Buy");
            buyButton.addActionListener(e -> {
                boolean success = controller.buyUpgrade(u);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Upgrade purchased!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough points or conditions not met.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(buyButton, BorderLayout.EAST);
            upgradesPanel.add(panel);
        }
    }
}
