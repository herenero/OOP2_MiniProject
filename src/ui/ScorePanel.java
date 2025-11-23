package ui;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {
    private int score = 0;
    private final JLabel scoreLabel = new JLabel("0");

    public ScorePanel() {
        setBackground(Color.YELLOW);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JLabel("Score: "));
        add(scoreLabel);
    }

    public void increase(int amount) {
        score += amount;
        scoreLabel.setText(Integer.toString(score));
    }

    public void reset() {
        score = 0;
        scoreLabel.setText("0");
    }

    public int getScore() {
        return score;
    }
}
