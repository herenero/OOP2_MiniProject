package controller;

import data.ScoreEntry;
import data.Word;
import difficulty.Difficulty;
import repository.interfaces.ScoreRepository;
import repository.interfaces.TextRepository;
import ui.GroundPanel;
import ui.ScorePanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

// 게임의 핵심 로직을 담당하는 컨트롤러 클래스
// 게임 루프, 단어 생성 및 이동, 충돌 감지, 점수 처리 등 수행
public class GameController {

    private final TextRepository textRepository;
    private final ScoreRepository scoreRepository;
    private final ScorePanel scorePanel;

    // 현재 화면에 떠있는 단어 목록. 여러 스레드에서 접근하므로 스레드 안전하게 구성
    private final List<Word> activeWords =
            Collections.synchronizedList(new ArrayList<Word>());

    // 게임 루프의 실행 상태를 제어하는 플래그. volatile 키워드로 가시성 보장
    private volatile boolean running = false;
    private volatile boolean paused = false; // 일시정지 상태 플래그
    private final Object pauseLock = new Object(); // 일시정지 동기화를 위한 락 객체
    private Thread gameLoopThread = null; // 게임 루프를 실행할 별도의 스레드

    private int orbitCenterX; // 플레이어(행성)의 중심 X 좌표
    private int orbitCenterY; // 플레이어(행성)의 중심 Y 좌표
    private int playerCollisionRadius = 0; // 플레이어의 충돌 반경

    private Difficulty currentDifficulty = Difficulty.EASY; // 현재 게임 난이도
    private Runnable onGameOver; // 게임 오버 시 실행될 콜백

    public GameController(TextRepository textRepository,
                          ScoreRepository scoreRepository,
                          ScorePanel scorePanel) {
        this.textRepository = textRepository;
        this.scoreRepository = scoreRepository;
        this.scorePanel = scorePanel;
    }

    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    public void setPlayerCollisionRadius(int radius) {
        this.playerCollisionRadius = radius;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    // 일시정지 상태 토글
    public void togglePause() {
        paused = !paused;
        if (!paused) {
            synchronized (pauseLock) {
                pauseLock.notify(); // 일시정지 해제 시 스레드 깨움
            }
        }
    }

    // 게임 시작
    public void startGame(final GroundPanel groundPanel, Difficulty difficulty) {
        if (running) { // 이미 게임이 실행 중이면 다시 시작하지 않음
            return;
        }

        if (difficulty == null) { // 난이도가 설정되지 않았으면 기본으로 Easy
            difficulty = Difficulty.EASY;
        }
        this.currentDifficulty = difficulty;

        running = true; // 게임 실행 상태로 변경
        paused = false; // 일시정지 상태 초기화
        activeWords.clear(); // 이전 게임의 단어 목록 초기화
        scorePanel.reset(); // 점수 초기화

        // 난이도에 따라 초기 단어 생성
        int initialCount = getInitialWordCountByDifficulty(difficulty);
        for (int i = 0; i < initialCount; i++) {
            spawnWord();
        }

        // 별도의 게임 루프 스레드를 시작하여 UI 스레드를 블로킹하지 않음
        gameLoopThread = new Thread(() -> gameLoop(groundPanel));
        gameLoopThread.start();
    }

    // 게임 중지
    public void stopGame() {
        running = false; // 게임 루프 종료 플래그 설정
        if (gameLoopThread != null && gameLoopThread.isAlive()) {
            // 스레드가 대기 상태일 경우 InterruptedException을 발생시켜 종료
            gameLoopThread.interrupt();
        }
    }

    // 현재 활성화된 단어 리스트의 스냅샷 반환
    // 렌더링 시점에 단어 리스트가 변경되는 것을 방지하기 위해 동기화 블록 사용
    public List<Word> getActiveWordsSnapshot() {
        // activeWords 리스트에 대한 동시 접근 제어
        synchronized (activeWords) {
            // 현재 상태의 복사본 반환
            return new ArrayList<Word>(activeWords);
        }
    }

    // 사용자의 입력 텍스트와 일치하는 단어가 있는지 검사
    public void checkInput(String input) {
        if (!running || paused) { // 게임이 실행 중이 아니거나 일시정지 상태면 처리하지 않음
            return;
        }
        if (input == null || input.trim().isEmpty()) { // 유효하지 않은 입력 처리
            return;
        }

        String trimmed = input.trim();
        Word matched = null;
        
        synchronized (activeWords) { // 단어 리스트 검색 및 제거 시 동시 접근 제어
            for (Word word : activeWords) {
                if (word.getText().equals(trimmed)) {
                    matched = word;
                    break;
                }
            }
            if (matched != null) {
                activeWords.remove(matched); // 일치하는 단어 제거
            }
        }

        // 단어를 맞췄을 경우 점수 증가 및 새 단어 생성
        if (matched != null) {
            util.SoundManager.playEffect("star-typing.wav"); // 효과음 재생
            scorePanel.increase(getScoreForDifficulty(currentDifficulty)); // 난이도별 점수 획득
            spawnWord(); // 새로운 단어 생성
        }
    }

    private int getScoreForDifficulty(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 10;
            case NORMAL -> 20;
            case HARD -> 30;
        };
    }

    // 사용자가 새로운 단어 추가
    public void addWordFromUser(String word) {
        textRepository.addWord(word);
    }

    // 모든 등록된 단어 목록 반환
    public List<String> getAllWords() {
        return textRepository.getAllWords();
    }

    // 점수 저장 다이얼로그를 띄우고 점수 저장
    public void saveScoreWithDialog() {
        int score = scorePanel.getScore();
        if (score <= 0) { // 점수가 없거나 0점이면 저장하지 않음
            return;
        }

        String name = JOptionPane.showInputDialog(
                null,
                "이름을 입력하세요:",
                "점수 저장",
                JOptionPane.PLAIN_MESSAGE
        );
        if (name == null) { // 입력 취소 시
            return;
        }
        String trimmed = name.trim();
        // 이름이 비어있을 경우
        if (trimmed.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "이름을 입력해주세요.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE
            );
        }
        ScoreEntry entry = new ScoreEntry(trimmed, score);
        scoreRepository.saveScore(entry); // 리포지토리에 점수 저장
    }

    // 상위 점수 목록을 포맷팅된 문자열로 반환
    public String getTopScoresText(int limit) {
        List<ScoreEntry> top = scoreRepository.loadTopScores(limit); // 상위 N개 점수 로드
        if (top.isEmpty()) {
            return "저장된 점수가 없습니다.";
        }
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (ScoreEntry entry : top) {
            sb.append(rank)
                    .append(". ")
                    .append(entry.getPlayerName())
                    .append(" - ")
                    .append(entry.getScore())
                    .append("\n");
            rank++;
        }
        return sb.toString();
    }

    // 게임의 메인 루프. 주기적으로 단어 위치를 업데이트하고 화면을 다시 그림
    private void gameLoop(final GroundPanel groundPanel) {
        try {
            while (running) { // running 플래그가 true인 동안 반복
                // 일시정지 상태 체크
                if (paused) {
                    synchronized (pauseLock) {
                        // 일시정지 상태면 화면에 PAUSED 표시 등을 위해 한 번 다시 그림
                        SwingUtilities.invokeLater(() -> {
                            groundPanel.setPaused(true);
                            groundPanel.repaint();
                        });
                        while (paused) {
                            pauseLock.wait(); // 일시정지 해제될 때까지 대기
                        }
                        // 일시정지 해제됨
                        SwingUtilities.invokeLater(() -> groundPanel.setPaused(false));
                    }
                }

                updateWords(); // 단어 위치 및 상태 업데이트
                // UI 업데이트는 반드시 EDT에서 이루어져야 하므로 SwingUtilities.invokeLater 사용
                // 프로젝트 하면서 새롭게 배운 메서드 참조 문법 사용
                SwingUtilities.invokeLater(groundPanel::repaint);

                Thread.sleep(16); // 약 60FPS를 유지하기 위한 대기 (1000ms / 60프레임 ≈ 16.6ms)
            }
        } catch (InterruptedException e) {
            // 스레드가 interrupt되면 루프 종료
            System.out.println("게임 루프 스레드 종료");
        }
    }

    // 모든 단어의 위치를 업데이트하고 충돌 여부 확인
    private void updateWords() {
        synchronized (activeWords) { // 단어 리스트 접근 시 동시성 제어
            double decaySpeed = getDecaySpeedByDifficulty(currentDifficulty); // 난이도에 따른 감소 속도
            boolean crashed = false;
            for (Word w : activeWords) {
                w.decreaseRadius(decaySpeed); // 플레이어를 향해 다가오게 함
                w.updatePosition(orbitCenterX, orbitCenterY); // 현재 궤도 중심에 따라 위치 업데이트

                // 플레이어(행성)와 충돌했는지 확인
                if (w.getRadius() <= playerCollisionRadius) {
                    crashed = true;
                }
            }
            if (crashed) {
                handleGameOver(); // 충돌 발생 시 게임 오버 처리
            }
        }
    }

    // 게임 오버 처리
    private void handleGameOver() {
        stopGame(); // 게임 루프 중지
        // 게임 오버 메시지 및 점수 저장 다이얼로그는 EDT에서 실행
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "게임 오버! 별똥별과 충돌했습니다.", "Game Over", JOptionPane.ERROR_MESSAGE);
            saveScoreWithDialog(); // 점수 저장
            if (onGameOver != null) {
                onGameOver.run(); // 게임 오버 콜백 실행 (메인 메뉴로 이동 등)
            }
        });
    }

    // 새 단어 생성 및 화면에 추가
    private void spawnWord() {
        String text;
        boolean isDuplicate;
        
        // 중복되지 않는 단어를 찾을 때까지 반복
        do {
            text = textRepository.getRandomWord();
            isDuplicate = false;
            synchronized (activeWords) {
                for (Word w : activeWords) {
                    if (w.getText().equals(text)) {
                        isDuplicate = true;
                        break;
                    }
                }
            }
        } while (isDuplicate);

        double radius = getRadiusByDifficulty(currentDifficulty); // 난이도별 초기 생성 거리
        double angle = Math.random() * Math.PI * 2; // 랜덤한 초기 각도
        double angularSpeed = getAngularSpeedByDifficulty(currentDifficulty); // 난이도별 각속도

        Word word = new Word(text, radius, angle, angularSpeed);
        synchronized (activeWords) { // 단어 리스트에 추가 시 동시성 제어
            activeWords.add(word);
        }
    }

    public void setOrbitCenter(int x, int y) {
        this.orbitCenterX = x;
        this.orbitCenterY = y;
    }

    // 난이도에 따른 초기 단어 수 반환
    private int getInitialWordCountByDifficulty(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 6;
            case NORMAL -> 7;
            case HARD -> 8;
        };
    }

    // 난이도에 따른 단어 생성 반경 반환 (더 큰 값일수록 플레이어에게서 멀리서 생성)
    private int getRadiusByDifficulty(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 240 + (int) (Math.random() * 40);
            case NORMAL -> 235 + (int) (Math.random() * 30);
            case HARD -> 230 + (int) (Math.random() * 30);
        };
    }

    // 난이도에 따른 단어의 궤도 회전 속도 반환
    private double getAngularSpeedByDifficulty(Difficulty difficulty) {
        double baseSpeed = switch (difficulty) {
            case EASY -> 0.01 + Math.random() * 0.01;
            case NORMAL -> 0.015 + Math.random() * 0.02;
            case HARD -> 0.017 + Math.random() * 0.02;
        };
        return 0.5 * baseSpeed;
    }

    // 난이도에 따른 단어의 플레이어 접근 속도 반환 (더 큰 값일수록 빠르게 다가옴)
    private double getDecaySpeedByDifficulty(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 0.11;
            case NORMAL -> 0.17;
            case HARD -> 0.2;
        };
    }
}