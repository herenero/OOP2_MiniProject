package ui;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

// 게임 플레이 화면 전체를 감싸는 패널
// 상단의 게임 영역(GroundPanel)과 하단의 입력 영역(InputPanel)을 포함
public class GamePanel extends JPanel {
    private final GroundPanel groundPanel;
    private final InputPanel inputPanel;

    public GamePanel(GameController controller) {
        setLayout(new BorderLayout());

        groundPanel = new GroundPanel(controller);
        inputPanel = new InputPanel(controller);

        add(groundPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public GroundPanel getGroundPanel() {
        return groundPanel;
    }
}