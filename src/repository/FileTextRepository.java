package repository;

import repository.interfaces.TextRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 텍스트 파일에서 단어 목록을 관리하는 리포지토리 구현체
public class FileTextRepository implements TextRepository {

    private final String filePath;
    private final List<String> words = new ArrayList<String>();
    private final Random random = new Random();

    public FileTextRepository(String filePath) {
        this.filePath = filePath;
        loadFromFile();
        // 파일에 단어가 없으면 기본 단어들을 생성해서 저장
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
        int index = random.nextInt(words.size());
        return words.get(index);
    }

    @Override
    public synchronized void addWord(String word) {
        if (word == null) {
            return;
        }
        String trimmed = word.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        words.add(trimmed);
        saveToFile();
    }

    @Override
    public synchronized List<String> getAllWords() {
        return new ArrayList<String>(words);
    }

    private void initDefaultWords() {
        // 기본 단어 초기화
        words.add("star");
        words.add("meteor");
        words.add("java");
        words.add("swing");
        words.add("thread");
        words.add("typing");
        words.add("galaxy");
        words.add("planet");
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
            System.err.println("word file 읽기 실패: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String word : words) {
                writer.write(word);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("word file 쓰기 실패: " + e.getMessage());
        }
    }
}