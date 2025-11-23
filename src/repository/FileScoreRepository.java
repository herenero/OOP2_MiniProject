package repository;

import data.ScoreEntry;
import repository.interfaces.ScoreRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileScoreRepository implements ScoreRepository {
    private final String filePath;

    public FileScoreRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public synchronized void saveScore(ScoreEntry entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(entry.getPlayerName() + "," + entry.getScore());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to save score: " + e.getMessage());
        }
    }

    @Override
    public synchronized List<ScoreEntry> loadTopScore(int limit) {
        List<ScoreEntry> list = new ArrayList<ScoreEntry>();
        var file = new File(filePath);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    String[] parts = trimmed.split(",");
                    if (parts.length == 2) {
                        String name = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        list.add(new ScoreEntry(name, score));
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to read score file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid score format: " + e.getMessage());
        }

        Collections.sort(list);
        if (list.size() > limit) {
            return new ArrayList<ScoreEntry>(list.subList(0, limit));
        }
        return list;
    }
}
