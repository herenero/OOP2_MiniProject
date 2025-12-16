package repository;

import data.ScoreEntry;
import repository.interfaces.ScoreRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 파일 시스템을 이용해 점수를 저장하고 불러오는 리포지토리 구현체
public class FileScoreRepository implements ScoreRepository {

    private final String filePath;

    public FileScoreRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public synchronized void saveScore(ScoreEntry entry) {
        if (entry == null) {
            return;
        }
        // 파일을 이어쓰기 모드로 열어서 점수 기록
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(entry.getPlayerName() + "," + entry.getScore());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("점수 저장 실패: " + e.getMessage());
        }
    }

    @Override
    public synchronized List<ScoreEntry> loadTopScores(int limit) {
        List<ScoreEntry> result = new ArrayList<ScoreEntry>();
        File file = new File(filePath);
        if (!file.exists()) {
            return result;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    String[] parts = trimmed.split(",");
                    if (parts.length == 2) {
                        String name = parts[0];
                        try {
                            int score = Integer.parseInt(parts[1]);
                            result.add(new ScoreEntry(name, score));
                        } catch (NumberFormatException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("score file 읽기 실패: " + e.getMessage());
        }

        // 점수순 정렬 후 상위 n개 반환
        Collections.sort(result);
        if (result.size() > limit) {
            return new ArrayList<ScoreEntry>(result.subList(0, limit));
        }
        return result;
    }
}