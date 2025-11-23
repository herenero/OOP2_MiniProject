package ui;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

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
