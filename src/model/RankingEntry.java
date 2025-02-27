package model;

import java.io.Serializable;

public class RankingEntry implements Serializable, Comparable<RankingEntry> {
    private String playerName;
    private int score;
    private int time;
    private String difficulty;

    public RankingEntry(String playerName, int score, int time, String difficulty) {
        this.playerName = playerName;
        this.score = score;
        this.time = time;
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return playerName + " | Points: " + score + " | Time: " + time + "s | Diff: " + difficulty;
    }

    @Override
    public int compareTo(RankingEntry o) {
        return Integer.compare(o.score, this.score);
    }
}
