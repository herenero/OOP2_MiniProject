package ui;

import data.ScoreEntry;
import repository.interfaces.ScoreRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

// 게임 화면 우측 하단에 위치하여 상위 점수 기록을 보여주는 패널
public class ScoreHistoryPanel extends JPanel {

    private final ScoreRepository scoreRepository; // 점수 데이터에 접근하기 위한 리포지토리
    private final JTextArea scoreArea;             // 점수 목록을 표시할 텍스트 영역

    public ScoreHistoryPanel(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;

        setLayout(new BorderLayout()); // BorderLayout을 사용하여 컴포넌트 배치
        setBackground(Color.YELLOW); // 배경색 노란색 설정
        setBorder(new EmptyBorder(10, 10, 10, 10)); // 패널 내부 여백 설정

        // 제목 라벨 설정
        JLabel titleLabel = new JLabel("기록 (Top 10)");
        titleLabel.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 14f).deriveFont(Font.BOLD)); // 폰트 적용
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // 중앙 정렬
        add(titleLabel, BorderLayout.NORTH); // 상단에 제목 추가

        // 점수 목록을 표시할 텍스트 영역 설정
        scoreArea = new JTextArea();
        scoreArea.setEditable(false); // 편집 불가능하도록 설정
        scoreArea.setBackground(new Color(255, 255, 224)); // 연한 노란색 배경
        scoreArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // 고정폭 폰트 사용 (정렬에 유리)
        
        JScrollPane scrollPane = new JScrollPane(scoreArea); // 스크롤 기능 추가
        scrollPane.setBorder(null); // 스크롤 패널 테두리 제거
        add(scrollPane, BorderLayout.CENTER); // 중앙에 점수 목록 스크롤 패널 추가

        // 패널 초기화 시 점수 목록 로드
        refreshScores();
    }

    // 점수 목록을 새로고침하여 화면에 표시
    public void refreshScores() {
        List<ScoreEntry> topScores = scoreRepository.loadTopScores(10); // 상위 10개 점수 로드
        StringBuilder sb = new StringBuilder();
        
        if (topScores.isEmpty()) {
            sb.append("기록된 점수가 없습니다."); // 점수가 없을 경우 메시지
        } else {
            int rank = 1;
            for (ScoreEntry entry : topScores) {
                // 각 점수 항목을 포맷팅하여 StringBuilder에 추가
                sb.append(String.format("%2d. %-10s : %d\n", rank++, entry.getPlayerName(), entry.getScore()));
            }
        }
        scoreArea.setText(sb.toString()); // 텍스트 영역에 점수 목록 설정
    }
}
