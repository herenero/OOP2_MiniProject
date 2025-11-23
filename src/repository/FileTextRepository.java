package repository;

import repository.interfaces.TextRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileTextRepository implements TextRepository {
    private final String filePath;
    private final List<String> words = new ArrayList<String>();
    private final Random random = new Random();

    public FileTextRepository(String filePath) {
        this.filePath = filePath;
        loadFromFile();
        if (words.isEmpty()) {
            initDefaultWords();
            saveToFile();
        }
    }

    @Override
    public synchronized String getRandomWord() {
        if (words.isEmpty()) {
            return "empty";
        }
        return words.get(random.nextInt(words.size()));
    }

    @Override
    public synchronized void addWord(String word) {
        words.add(word);
        saveToFile();
    }

    @Override
    public synchronized List<String> getAllWords() {
        return new ArrayList<String>(words);
    }

    private void initDefaultWords() {
        words.add("java");
        words.add("swing");
        words.add("thread");
        words.add("typing");
        words.add("game");
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    words.add(trimmed);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to read words file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String word : words) {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save words file: " + e.getMessage());
        }
    }
}
