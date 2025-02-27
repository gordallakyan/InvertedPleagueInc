package model;

public class RailConnection extends TransportConnection {
    public RailConnection(Country from, Country to) {
        super(from, to);
    }

    @Override
    public double getInfectionRateModifier() {
        return 1.2;
    }
}
