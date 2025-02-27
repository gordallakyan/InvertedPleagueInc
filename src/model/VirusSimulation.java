package model;

import controller.GameController;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VirusSimulation {
    private List<Country> countries;
    private List<TransportConnection> connections;
    private Timer timer;
    private String difficulty = "Medium";
    private double baseInfectionRate = 0.02;
    private int lastTickNewInfections = 0;
    private GameController controller;

    public VirusSimulation(List<Country> countries, List<TransportConnection> connections, GameController ctrl) {
        this.countries = countries;
        this.connections = connections;
        this.controller = ctrl;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
        switch (difficulty) {
            case "Easy":
                baseInfectionRate = 0.05;
                break;
            case "Medium":
                baseInfectionRate = 0.01;
                break;
            case "Hard":
                baseInfectionRate = 0.02;
                break;
        }
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void start(Runnable callbackOnChange) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                spreadVirus();
                healRandomly();
                callbackOnChange.run();
            }
        }, 1000, 2000);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public int getLastTickNewInfections() {
        return lastTickNewInfections;
    }

    private void spreadVirus() {
        lastTickNewInfections = 0;
        for (TransportConnection conn : connections) {
            if (!conn.isOpen()) continue;
            Country from = conn.getFrom();
            Country to = conn.getTo();
            long infFrom = from.getInfected();
            if (infFrom == 0) continue;
            int increase = (int)(infFrom * baseInfectionRate * conn.getInfectionRateModifier());
            long oldInf = to.getInfected();
            long newInf = Math.min(to.getPopulation(), oldInf + increase);
            lastTickNewInfections += (newInf - oldInf);
            to.setInfected(newInf);
        }
    }

    private void healRandomly() {
        for (Country c : countries) {
            if (c.getInfected() > 0) {
                long heal = (long) (c.getInfected() * 0.001);
                long newInf = Math.max(0, c.getInfected() - heal);
                c.setInfected(newInf);
            }
        }
    }
}
