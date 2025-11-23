package controller;

import data.ScoreEntry;
import data.Word;
import repository.interfaces.ScoreRepository;
import repository.interfaces.TextRepository;
import ui.GroundPanel;
import ui.ScorePanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {
    private final TextRepository textRepository;
    private final ScoreRepository scoreRepository;
    private final ScorePanel scorePanel;

    private final List<Word> activeWords = Collections.synchronizedList(new ArrayList<Word>());

    private volatile boolean isRunning = false;
    private Thread gameLoopThread = null;

    private int groundWidth = 400;
    private int groundHeight = 400;

    public GameController(TextRepository textRepository, ScoreRepository scoreRepository, ScorePanel scorePanel) {
        this.textRepository = textRepository;
        this.scoreRepository = scoreRepository;
        this.scorePanel = scorePanel;
    }

    public void setGroundSize(int width, int height) {
        this.groundWidth = width;
        this.groundHeight = height;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void startGame(GroundPanel groundPanel) {
        if (isRunning) {
            return;
        }

        isRunning = true;
        activeWords.clear();
        scorePanel.reset();

        // 초기 단어 몇 개 생성
        for (int i = 0; i < 3; i++) {
            spawnWord();
        }

        gameLoopThread = new Thread(() -> gameLoop(groundPanel));

        gameLoopThread.start();
    }

    public void stopGame() {
        isRunning = false;
        if (gameLoopThread != null && gameLoopThread.isAlive()) {
            gameLoopThread.interrupt();
        }
    }

    public List<Word> getActiveWordsSnapshot() {
        synchronized (activeWords) {
            return new ArrayList<Word>(activeWords);
        }
    }

    public void checkInput(String input) {
        if (!isRunning) {
            return;
        }

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String trimmed = input.trim();

        Word matched = null;

        synchronized (activeWords) {
            for (Word word : activeWords) {
                if (word.getWord().equals(trimmed)) {
                    matched = word;
                    break;
                }
            }
            if (matched != null) {
                activeWords.remove(matched);
            }
        }

        if (matched != null) {
            scorePanel.increase(10);
            spawnWord();
        }
    }


    private void gameLoop(final GroundPanel groundPanel) {
        try {
            while (isRunning) {
                updateWords();
                SwingUtilities.invokeLater(() -> groundPanel.repaint());

                Thread.sleep(50); // 20 FPS 정도
            }
        } catch (InterruptedException e) {
            // 스레드 종료
            System.out.println("Game loop interrupted");
        }
    }

    private void updateWords() {
        List<Word> toRemove = new ArrayList<Word>();

        synchronized (activeWords) {
            for (Word word : activeWords) {
                word.moveDown();
                if (word.getY() > groundHeight) {
                    toRemove.add(word);
                }
            }

            for (Word word : toRemove) {
                activeWords.remove(word);
            }
        }

        // 바닥에 떨어진 단어만큼 새 단어 생성
        for (int i = 0; i < toRemove.size(); i++) {
            spawnWord();
        }
    }

    private void spawnWord() {
        String text = textRepository.getRandomWord();
        int x = (int) (Math.random() * (groundWidth - 80)) + 20;
        int y = 0;
        int speed = 3 + (int) (Math.random() * 4); // 3~6 픽셀

        Word word = new Word(text, x, y, speed);

        synchronized (activeWords) {
            activeWords.add(word);
        }
    }

    public void addWordFromUser(String word) {
        if (word == null) {
            return;
        }
        String trimmed = word.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        textRepository.addWord(trimmed);
    }

    public void saveScore(String playerName) {
        int score = scorePanel.getScore();
        if (score > 0) {
            scoreRepository.saveScore(new ScoreEntry(playerName, score));
        }
    }
}
