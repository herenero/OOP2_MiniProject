package repository.interfaces;

import data.ScoreEntry;

import java.util.List;

// 점수 저장소 인터페이스
public interface ScoreRepository {
    // 점수 저장
    void saveScore(ScoreEntry entry);

    // 상위 점수 목록 불러오기
    // limit: 불러올 최대 개수
    List<ScoreEntry> loadTopScores(int limit);
}