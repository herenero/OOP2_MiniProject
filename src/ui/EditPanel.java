package ui;

import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditPanel extends JPanel {
    private final JButton addButton = new JButton("단어 추가");
    private final JTextField editField = new JTextField(10);

    public EditPanel(GameController controller) {
        setBackground(Color.CYAN);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(new JLabel("새 단어: "));
        add(editField);
        add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = editField.getText();
                controller.addWordFromUser(text);
                editField.setText("");
                JOptionPane.showMessageDialog(EditPanel.this,
                        "단어가 추가되었습니다.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
