package ui;

import controller.GameController;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

// 단어 추가 화면(패널) 클래스
// 사용자가 새로운 단어를 입력하여 게임 데이터에 추가할 수 있으며, 현재 등록된 단어 목록도 함께 보여줌
// 메인 메뉴에서 호출될 때 전체 화면으로 나타남
public class EditPanel extends JPanel {

    private final GameController controller;        // 게임 로직을 처리하는 컨트롤러
    private final JTextField wordField = new JTextField(12); // 새 단어를 입력받는 텍스트 필드
    private final JButton addButton = new JButton("단어 추가"); // 단어 추가 버튼
    private final JTextArea wordListArea = new JTextArea(); // 등록된 단어 목록을 표시하는 텍스트 영역

    public EditPanel(GameController controller) {
        this.controller = controller;
        setBackground(Color.LIGHT_GRAY); // 패널 전체 배경색을 노란색으로 설정
        setLayout(new BorderLayout()); // BorderLayout을 사용하여 컴포넌트 배치

        // 상단 패널: 단어 입력 폼
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.DARK_GRAY); // 상단 패널 배경색도 노란색
        
        JLabel newWordLabel = new JLabel("새 단어: ");
        newWordLabel.setForeground(Color.WHITE); // 라벨 텍스트 색상 검정
        newWordLabel.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 14f).deriveFont(Font.PLAIN));
        
        topPanel.add(newWordLabel); // 라벨 추가
        topPanel.add(wordField);    // 입력 필드 추가
        topPanel.add(addButton);    // 추가 버튼 추가
        
        add(topPanel, BorderLayout.NORTH); // 상단에 입력 폼 패널 배치

        // 중앙 패널: 단어 목록 표시 영역
        wordListArea.setEditable(false); // 텍스트 영역 편집 불가
        wordListArea.setBackground(Color.DARK_GRAY);
        wordListArea.setForeground(Color.WHITE); // 단어 텍스트 색상 흰색
        wordListArea.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 14f));
        JScrollPane scrollPane = new JScrollPane(wordListArea); // 스크롤 기능 추가
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // 스크롤 패널 여백 추가
        
        add(scrollPane, BorderLayout.CENTER); // 중앙에 단어 목록 스크롤 패널 배치

        // 패널 초기화 시 단어 목록 로드
        refreshWordList();

        // '단어 추가' 버튼 액션 리스너
        addButton.addActionListener(e -> {
            String text = wordField.getText();
            if (text != null && !text.trim().isEmpty()) { // 입력이 유효한 경우
                controller.addWordFromUser(text); // 컨트롤러를 통해 단어 추가
                wordField.setText(""); // 입력 필드 초기화
                JOptionPane.showMessageDialog(
                        this,
                        "단어 '" + text + "'가 추가되었습니다.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refreshWordList(); // 단어 목록 새로고침
            }
        });
    }

    // 단어 목록을 컨트롤러에서 가져와 JTextArea에 표시
    private void refreshWordList() {
        List<String> words = controller.getAllWords(); // 모든 단어 목록 가져오기
        StringBuilder sb = new StringBuilder();
        sb.append("=== 현재 등록된 단어 목록 ===\n\n");
        if (words.isEmpty()) {
            sb.append("등록된 단어가 없습니다.");
        } else {
            for (String word : words) {
                sb.append("- ").append(word).append("\n"); // 각 단어를 포맷하여 추가
            }
        }
        wordListArea.setText(sb.toString()); // JTextArea에 텍스트 설정
        wordListArea.setCaretPosition(0); // 스크롤을 맨 위로 이동
    }
}