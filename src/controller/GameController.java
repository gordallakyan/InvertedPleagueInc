package controller;

import model.GameModel;
import model.Country;
import model.Upgrade;
import view.GameView;
import view.RankingView;
import view.MainMenuView;

import javax.swing.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private GameModel model;
    private GameView gameView;
    private Timer gameTimer;
    private int elapsedTime = 0;

    public GameController() {
        model = new GameModel();
    }

    public void startNewGame(String difficulty) {
        elapsedTime = 0;
        model.setupGame(difficulty);
        model.createSimulationWithController(this);
        gameView = new GameView(this, model.getCountries(), model.getConnections());
        gameView.setVisible(true);
        startGameTimer();
        model.startVirusSimulation(() -> {
            SwingUtilities.invokeLater(() -> {
                gameView.updatePoints(model.getPoints());
                gameView.updateStatus(model.getStatusString());
                gameView.refreshMap();
                checkGameOver();
            });
        });
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                elapsedTime++;
                SwingUtilities.invokeLater(() -> {
                    gameView.updateTime(elapsedTime);
                    checkGameOver();
                });
            }
        }, 1000, 1000);
    }

    private void endGame(String message) {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        model.stopVirusSimulation();
        JOptionPane.showMessageDialog(gameView, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        String playerName = JOptionPane.showInputDialog(gameView, "Enter your name for the ranking:");
        if (playerName != null && !playerName.isEmpty()) {
            model.saveRanking(playerName, elapsedTime);
        }
        backToMainMenu(gameView);
    }

    private void checkGameOver() {
        if (model.isAllInfected()) {
            endGame("All are infected. You lose!");
        } else if (model.isNoInfected()) {
            endGame("No infected remain. You win!");
        } else if (model.isNoSpreadLongTime()) {
            endGame("Virus has not spread for a long time. You win!");
        }
    }

    public void backToMainMenu(JFrame currentFrame) {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        model.stopVirusSimulation();
        currentFrame.dispose();
        SwingUtilities.invokeLater(() -> {
            MainMenuView menu = new MainMenuView();
            menu.setVisible(true);
        });
    }

    public void countryClicked(Country c, JFrame parent) {
        JOptionPane.showMessageDialog(parent,
                "Kraj: " + c.getName() +
                        "\nPopulation: " + c.getPopulation() +
                        "\nInfected: " + c.getInfected(),
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        );
        int confirm = JOptionPane.showConfirmDialog(parent,
                "Close connections with " + c.getName() + "? (+10 pts)",
                "Close Borders",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            model.closeConnectionsFrom(c);
            model.addPoints(10);
            gameView.updatePoints(model.getPoints());
            gameView.refreshMap();
        }
    }

    public List<Upgrade> getAvailableUpgrades() {
        return model.getUpgrades();
    }

    public boolean buyUpgrade(Upgrade u) {
        boolean ok = model.buyUpgrade(u);
        if (ok) {
            gameView.updatePoints(model.getPoints());
        }
        return ok;
    }

    public void showRanking(JFrame parent) {
        try {
            List<model.RankingEntry> rank = model.loadRanking();
            RankingView rankingView = new RankingView(rank);
            rankingView.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Error loading ranking: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
