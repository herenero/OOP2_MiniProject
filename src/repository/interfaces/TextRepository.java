package repository.interfaces;

import java.util.List;

public interface TextRepository {
    String getRandomWord();
    void addWord(String word);
    List<String> getAllWords();
}
