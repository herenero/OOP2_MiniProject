package repository.interfaces;

import data.ScoreEntry;

import java.util.List;

public interface ScoreRepository {
    void saveScore(ScoreEntry entry);
    List<ScoreEntry> loadTopScore(int limit);
}
