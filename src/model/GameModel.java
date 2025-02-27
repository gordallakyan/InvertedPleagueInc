package model;

import controller.GameController;
import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private List<Country> countries;
    private List<TransportConnection> connections;
    private VirusSimulation simulation;
    private UpgradeManager upgradeManager;
    private RankingManager rankingManager;
    private long points;
    private String chosenDifficulty = "Medium";
    private int noSpreadTicks;

    public GameModel() {
        countries = new ArrayList<>();
        connections = new ArrayList<>();
        rankingManager = new RankingManager("scores.ser");
        points = 0L;
        noSpreadTicks = 0;
    }

    private Country getByName(String name) {
        for (Country c : countries) {
            if (c.getName().equals(name)) return c;
        }
        return null;
    }

    public void setupGame(String difficulty) {
        chosenDifficulty = difficulty;
        countries.clear();
        connections.clear();
        countries.add(new Country("Canada",   37000000L,   0L, 130,  90));
        countries.add(new Country("USA",     330000000L,   0L, 160, 180));
        countries.add(new Country("Brazil",  209000000L,   0L, 300, 420));
        countries.add(new Country("UK",       66000000L,   0L, 600, 130));
        countries.add(new Country("Germany",  83000000L,   0L, 695,  80));
        countries.add(new Country("France",   67000000L,   0L, 550, 270));
        countries.add(new Country("Spain",    47000000L,   0L, 620, 430));
        countries.add(new Country("Poland",   38000000L,   0L, 780, 200));
        countries.add(new Country("Italy",    60000000L,   0L, 920, 210));
        countries.add(new Country("China", 1393000000L, 1000L, 1050, 450));
        connections.add(new AirConnection(getByName("Canada"), getByName("Germany")));
        connections.add(new AirConnection(getByName("Germany"), getByName("Canada")));
        connections.add(new SeaConnection(getByName("Canada"), getByName("Germany")));
        connections.add(new SeaConnection(getByName("Germany"), getByName("Canada")));
        connections.add(new RoadConnection(getByName("Brazil"), getByName("USA")));
        connections.add(new RoadConnection(getByName("USA"),    getByName("Brazil")));
        connections.add(new AirConnection(getByName("Brazil"),  getByName("USA")));
        connections.add(new AirConnection(getByName("USA"),     getByName("Brazil")));
        connections.add(new AirConnection(getByName("China"),   getByName("Italy")));
        connections.add(new AirConnection(getByName("Italy"),   getByName("China")));
        connections.add(new SeaConnection(getByName("China"),   getByName("Italy")));
        connections.add(new SeaConnection(getByName("Italy"),   getByName("China")));
        connections.add(new AirConnection(getByName("China"),   getByName("Spain")));
        connections.add(new AirConnection(getByName("Spain"),   getByName("China")));
        connections.add(new SeaConnection(getByName("China"),   getByName("Spain")));
        connections.add(new SeaConnection(getByName("Spain"),   getByName("China")));
        connections.add(new RoadConnection(getByName("Canada"), getByName("USA")));
        connections.add(new RoadConnection(getByName("USA"),    getByName("Canada")));
        connections.add(new RoadConnection(getByName("Germany"),getByName("Spain")));
        connections.add(new RoadConnection(getByName("Spain"),  getByName("Germany")));
        connections.add(new AirConnection(getByName("USA"),     getByName("UK")));
        connections.add(new AirConnection(getByName("UK"),      getByName("USA")));
        connections.add(new AirConnection(getByName("France"),  getByName("Poland")));
        connections.add(new AirConnection(getByName("Poland"),  getByName("France")));
        connections.add(new AirConnection(getByName("China"),   getByName("Germany")));
        connections.add(new AirConnection(getByName("Germany"), getByName("China")));
        connections.add(new AirConnection(getByName("UK"),      getByName("France")));
        connections.add(new AirConnection(getByName("France"),  getByName("UK")));
        connections.add(new AirConnection(getByName("France"),  getByName("Germany")));
        connections.add(new AirConnection(getByName("Germany"), getByName("France")));
        connections.add(new SeaConnection(getByName("Spain"),   getByName("Italy")));
        connections.add(new SeaConnection(getByName("Italy"),   getByName("Spain")));
        connections.add(new SeaConnection(getByName("Poland"),  getByName("Brazil")));
        connections.add(new SeaConnection(getByName("Brazil"),  getByName("Poland")));
        upgradeManager = new UpgradeManager();
        points = 0L;
        noSpreadTicks = 0;
    }

    public void createSimulationWithController(GameController ctrl) {
        simulation = new VirusSimulation(countries, connections, ctrl);
        simulation.setDifficulty(chosenDifficulty);
    }

    public void startVirusSimulation(Runnable callbackOnChange) {
        if (simulation == null) return;
        simulation.start(() -> {
            int newlyInfected = simulation.getLastTickNewInfections();
            if (newlyInfected > 0 && newlyInfected < 1000) {
                points++;
            }
            if (newlyInfected == 0) {
                noSpreadTicks++;
            } else {
                noSpreadTicks = 0;
            }
            callbackOnChange.run();
        });
    }

    public void stopVirusSimulation() {
        if (simulation != null) simulation.stop();
    }

    public boolean isAllInfected() {
        return countries.stream().allMatch(c -> c.getInfected() == c.getPopulation());
    }

    public boolean isNoInfected() {
        return countries.stream().allMatch(c -> c.getInfected() == 0);
    }

    public boolean isNoSpreadLongTime() {
        return noSpreadTicks >= 5;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public List<TransportConnection> getConnections() {
        return connections;
    }

    public long getPoints() {
        return points;
    }

    public void addPoints(long p) {
        points += p;
    }

    public String getStatusString() {
        long totalPop = countries.stream().mapToLong(Country::getPopulation).sum();
        long totalInf = countries.stream().mapToLong(Country::getInfected).sum();
        return "Infected: " + totalInf + "/" + totalPop;
    }

    public void saveRanking(String playerName, int time) {
        RankingEntry entry = new RankingEntry(playerName, (int)Math.min(points, Integer.MAX_VALUE), time, chosenDifficulty);
        rankingManager.saveEntry(entry);
    }

    public List<RankingEntry> loadRanking() {
        return rankingManager.loadEntries();
    }

    public void closeConnectionsFrom(Country c) {
        for (TransportConnection conn : connections) {
            if (conn.getFrom() == c || conn.getTo() == c) {
                conn.setOpen(false);
            }
        }
    }

    public List<Upgrade> getUpgrades() {
        return upgradeManager.getUpgrades();
    }

    public boolean buyUpgrade(Upgrade u) {
        if (points >= u.getCost()) {
            points -= u.getCost();
            u.apply();
            return true;
        }
        return false;
    }
}
