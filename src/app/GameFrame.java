package app;

import controller.GameController;
import repository.FileScoreRepository;
import repository.FileTextRepository;
import repository.interfaces.ScoreRepository;
import repository.interfaces.TextRepository;
import ui.EditPanel;
import ui.GamePanel;
import ui.ScorePanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final ScorePanel scorePanel;
    private final EditPanel editPanel;
    private final GamePanel gamePanel;

    private final GameController controller;

    private final JMenuItem startItem = new JMenuItem("Start");
    private final JMenuItem stopItem = new JMenuItem("Stop");
    private final JButton startButton = new JButton("Start");
    private final JButton stopButton = new JButton("Stop");

    public GameFrame() {
        super("Game");

        TextRepository textRepository = new FileTextRepository("words.txt");
        ScoreRepository scoreRepository = new FileScoreRepository("scores.txt");

        scorePanel = new ScorePanel();
        controller = new GameController(textRepository, scoreRepository, scorePanel);
        gamePanel = new GamePanel(controller);
        editPanel = new EditPanel(controller);

        setSize(Main.WIDTH, Main.HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        makeMenu();
        makeToolBar();
        makeSplitPane();
        setVisible(true);
    }

    private void makeToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(startButton);
        toolBar.add(stopButton);

        startButton.addActionListener(e -> controller.startGame(gamePanel.getGroundPanel()));

        stopButton.addActionListener(e -> controller.stopGame());

        getContentPane().add(toolBar, BorderLayout.NORTH);
    }

    private void makeSplitPane() {
        JSplitPane hPane = new JSplitPane();
        hPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        hPane.setDividerLocation(500);
        hPane.setEnabled(false);
        getContentPane().add(hPane, BorderLayout.CENTER);

        JSplitPane vPane = new JSplitPane();
        vPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        vPane.setDividerLocation(200);
        vPane.setEnabled(false);

        vPane.setTopComponent(scorePanel);
        vPane.setBottomComponent(editPanel);

        hPane.setLeftComponent(gamePanel);
        hPane.setRightComponent(vPane);
    }

    private void makeMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        gameMenu.add(startItem);
        gameMenu.add(stopItem);

        startItem.addActionListener(e -> controller.startGame(gamePanel.getGroundPanel()));
        stopItem.addActionListener(e -> controller.stopGame());

        JMenu fileMenu = new JMenu("Edit");
        menuBar.add(fileMenu);

        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.addActionListener(e -> {
            if (controller.isRunning()) {
                int confirm = JOptionPane.showConfirmDialog(
                        GameFrame.this,
                        "게임을 종료하시겠습니까?\n점수를 저장할까요?",
                        "Exit",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );

                if (confirm == JOptionPane.CANCEL_OPTION) {
                    return; // 종료 취소
                }

                if (confirm == JOptionPane.YES_OPTION) {
                    String name = JOptionPane.showInputDialog("이름을 입력하세요:");
                    if (name != null && !name.isEmpty()) {
                        controller.saveScore(name);
                    }
                }
            }

            controller.stopGame();
            dispose();
        });

        fileMenu.add(exitMenu);
    }
}
