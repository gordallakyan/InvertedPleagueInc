package model;

public class AirConnection extends TransportConnection {
    public AirConnection(Country from, Country to) {
        super(from, to);
    }

    @Override
    public double getInfectionRateModifier() {
        return 1.5;
    }
}
