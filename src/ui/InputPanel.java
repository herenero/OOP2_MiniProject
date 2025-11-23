package ui;

import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputPanel extends JPanel {
    private final JTextField inputField = new JTextField(20);

    public InputPanel(GameController controller) {
        setBackground(Color.LIGHT_GRAY);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(new JLabel("Input: "));
        add(inputField);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputField.getText();
                controller.checkInput(text);
                inputField.setText("");
            }
        });
    }
}
