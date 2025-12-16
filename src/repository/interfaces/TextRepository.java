package repository.interfaces;

import java.util.List;

// 단어 저장소 인터페이스
public interface TextRepository {
    // 저장소에서 무작위 단어 하나를 가져옴
    String getRandomWord();

    // 새로운 단어를 저장소에 추가
    void addWord(String word);

    // 모든 단어 목록 반환
    List<String> getAllWords();
}