package model;

import java.awt.*;

public class Country {
    private String name;
    private long population;
    private long infected;
    private int x, y;

    // Nowe pole, by śledzić, kiedy kraj został zainfekowany po raz pierwszy.
    // -1 => jeszcze nie zainfekowano.
    private int infectionStartTime = -1;

    public Country(String name, long population, long infected, int x, int y) {
        this.name = name;
        this.population = population;
        this.infected = infected;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }
    public long getPopulation() {
        return population;
    }
    public long getInfected() {
        return infected;
    }
    public void setInfected(long infected) {
        this.infected = infected;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    /**
     * Ustawiamy czas pierwszego zakażenia (tylko raz).
     */
    public void recordInfectionStart(int currentTime) {
        if (infectionStartTime < 0 && infected > 0) {
            infectionStartTime = currentTime;
        }
    }

    /**
     * Metoda rysująca kraj.
     * Zmniejszamy np. rozmiar kwadratu do 30x30 (lub inny wedle uznania).
     */
    public void draw(Graphics g) {
        int size = 30;
        float ratio = (float) infected / (float) population;
        Color c = ratio == 0 ? Color.GREEN : (ratio >= 1.0 ? Color.RED : Color.YELLOW);

        g.setColor(c);
        g.fillRect(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size);
        g.drawString(name, x, y + size + 12);
    }

    /**
     * Logika zamykania granic w zależności od:
     *  - nazwy kraju,
     *  - czasu od pierwszego zakażenia,
     *  - procentu zakażeń,
     *  - wielkości populacji.
     *
     * @param currentTime aktualny czas (sekundy) od rozpoczęcia gry.
     */
    public boolean canCloseBorders(int currentTime) {
        double ratio = (double) infected / (double) population;

        switch (name) {
            case "France":
                // Warunek: musi być jakiekolwiek zakażenie, a od firstInfection min. 200 sek
                if (infectionStartTime >= 0 && (currentTime - infectionStartTime) >= 200) {
                    return true;
                }
                return false;

            case "Spain":
                // Warunek: 30% (0.3)
                return (ratio >= 0.3);

            default:
                // Zależnie od populacji => min. 20%, max. 40%
                // np. > 100 mln => 20%, w innym razie => 40%
                if (population > 100_000_000L) {
                    // duży kraj -> 20%
                    return (ratio >= 0.2);
                } else {
                    // mniejszy kraj -> 40%
                    return (ratio >= 0.4);
                }
        }
    }
}
