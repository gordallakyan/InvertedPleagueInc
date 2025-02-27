package model;

public class Upgrade {
    private String name;
    private int cost;
    private Runnable effect;

    public Upgrade(String name, int cost, Runnable effect) {
        this.name = name;
        this.cost = cost;
        this.effect = effect;
    }

    public String getName() { return name; }
    public int getCost() { return cost; }

    public void apply() {
        effect.run();
    }
}
