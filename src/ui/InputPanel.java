package ui;

import controller.GameController;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.FlowLayout;

// 사용자 입력을 받는 하단 패널
public class InputPanel extends JPanel {

    private final JTextField inputField = new JTextField(20);

    public InputPanel(GameController controller) {
        setBackground(Color.DARK_GRAY);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel inputLabel = new JLabel("입력: ");
        inputLabel.setForeground(Color.WHITE);
        add(inputLabel);
        
        // 입력 필드 추가
        add(inputField);
        inputField.setForeground(Color.BLACK); // 입력 텍스트 색상 (기본값)

        // 엔터 키 입력 시 단어 확인
        inputField.addActionListener(e -> {
            String text = inputField.getText();
            controller.checkInput(text);
            inputField.setText("");
        });
    }
}