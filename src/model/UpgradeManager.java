package model;

import java.util.ArrayList;
import java.util.List;

public class UpgradeManager {
    private List<Upgrade> upgrades;
    private boolean closeEarlierPurchased = false;
    private boolean fasterCuringPurchased = false;
    private boolean betterDetectionPurchased = false;
    private boolean vaccineAccelerationPurchased = false;
    private boolean internationalCollabPurchased = false;

    public UpgradeManager() {
        upgrades = new ArrayList<>();
        upgrades.add(new Upgrade("Close Borders Even Earlier", 30, () -> {
            closeEarlierPurchased = true;
        }));
        upgrades.add(new Upgrade("Faster Curing", 50, () -> {
            fasterCuringPurchased = true;
        }));
        upgrades.add(new Upgrade("Better Detection", 75, () -> {
            betterDetectionPurchased = true;
        }));
        upgrades.add(new Upgrade("Global Vaccine Acceleration", 100, () -> {
            vaccineAccelerationPurchased = true;
        }));
        upgrades.add(new Upgrade("International Collaboration", 120, () -> {
            internationalCollabPurchased = true;
        }));
        upgrades.add(new Upgrade("Enhanced Border Control", 60, () -> { }));
        upgrades.add(new Upgrade("Advanced Medical Research", 90, () -> { }));
        upgrades.add(new Upgrade("Mass Testing", 70, () -> { }));
        upgrades.add(new Upgrade("Quarantine Enforcement", 40, () -> { }));
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public int getCureSpeedBonus() {
        int bonus = 0;
        if (vaccineAccelerationPurchased) bonus += 1;
        if (internationalCollabPurchased) bonus += 1;
        return bonus;
    }
}
