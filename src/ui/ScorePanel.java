package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// 현재 점수를 표시하는 패널
public class ScorePanel extends JPanel {

    private int score = 0; // 현재 점수
    private final JLabel titleLabel = new JLabel("점수"); // "점수" 텍스트 라벨
    private final JLabel scoreLabel = new JLabel("0"); // 점수를 표시할 라벨

    public ScorePanel() {
        setBackground(Color.YELLOW); // 배경색 노란색 설정
        setLayout(new BorderLayout()); // BorderLayout 사용

        // 여백 설정
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 중앙 패널 생성
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.YELLOW);

        // "점수" 제목 라벨 설정
        titleLabel.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 16f).deriveFont(Font.PLAIN));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 실제 점수 라벨 설정
        scoreLabel.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 32f).deriveFont(Font.BOLD));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10)); // 제목과 점수 사이 간격
        centerPanel.add(scoreLabel);

        add(centerPanel, BorderLayout.CENTER); // 라벨을 패널 중앙에 추가
    }

    // 점수를 amount만큼 증가시키고 화면을 업데이트
    public void increase(int amount) {
        score += amount;
        updateLabel();
    }

    // 점수를 0으로 초기화하고 화면을 업데이트
    public void reset() {
        score = 0;
        updateLabel();
    }

    // 점수 라벨의 텍스트를 현재 점수에 맞춰 업데이트
    private void updateLabel() {
        scoreLabel.setText(String.valueOf(score));
    }

    public int getScore() {
        return score;
    }
}