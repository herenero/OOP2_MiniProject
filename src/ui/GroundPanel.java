package ui;

import controller.GameController;
import data.Word;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class GroundPanel extends JPanel {
    private final GameController controller;

    public GroundPanel(GameController controller) {
        this.controller = controller;
        setBackground(Color.WHITE);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = getSize();
                controller.setGroundSize(size.width, size.height);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Word> words = controller.getActiveWordsSnapshot();
        for (Word word : words) {
            g.drawString(word.getWord(), word.getX(), word.getY());
        }
    }
}
