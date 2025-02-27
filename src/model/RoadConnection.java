package model;

public class RoadConnection extends TransportConnection {
    public RoadConnection(Country from, Country to) {
        super(from, to);
    }

    @Override
    public double getInfectionRateModifier() {
        return 1.0;
    }
}
