package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingManager {
    private String filename;

    public RankingManager(String filename) {
        this.filename = filename;
    }

    public void saveEntry(RankingEntry entry) {
        List<RankingEntry> entries = loadEntries();
        entries.add(entry);
        Collections.sort(entries);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<RankingEntry> loadEntries() {
        File file = new File(filename);
        if(!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<RankingEntry>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
