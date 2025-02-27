package model;

public abstract class TransportConnection {
    protected Country from;
    protected Country to;
    protected boolean open = true;

    public TransportConnection(Country from, Country to) {
        this.from = from;
        this.to = to;
    }

    public abstract double getInfectionRateModifier();

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
    public Country getFrom() { return from; }
    public Country getTo() { return to; }
}
