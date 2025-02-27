package model;

public class SeaConnection extends TransportConnection {
    public SeaConnection(Country from, Country to) {
        super(from, to);
    }

    @Override
    public double getInfectionRateModifier() {
        return 1.3;
    }
}
