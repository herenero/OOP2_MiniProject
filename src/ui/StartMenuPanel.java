package ui;

import app.GameFrame;

import util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

// 게임 시작 메뉴 화면 패널
// 게임 시작, 난이도 설정, 기록 보기 등의 메뉴 버튼 제공
public class StartMenuPanel extends JPanel {

    private final BufferedImage backgroundImage;

    public StartMenuPanel(GameFrame frame) {
        backgroundImage = ImageLoader.getImage("background_menu.jpg");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 제목 레이블 설정
        JLabel title = new JLabel("별똥별 사냥꾼");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 30f).deriveFont(Font.BOLD));
        title.setForeground(Color.WHITE);

        // 메뉴 버튼 생성
        JButton startButton = new JButton("게임 시작");
        JButton difficultyButton = new JButton("난이도 설정");
        JButton addWordButton = new JButton("단어 추가");
        JButton scoreButton = new JButton("기록 보기");
        JButton exitButton = new JButton("나가기");

        // 버튼 폰트 설정
        startButton.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 15f));
        difficultyButton.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 15f));
        addWordButton.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 15f));
        scoreButton.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 15f));
        exitButton.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 15f));


        // 버튼 정렬 설정
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addWordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 버튼 크기 설정
        startButton.setMaximumSize(new Dimension(200, 40));
        difficultyButton.setMaximumSize(new Dimension(200, 40));
        addWordButton.setMaximumSize(new Dimension(200, 40));
        scoreButton.setMaximumSize(new Dimension(200, 40));
        exitButton.setMaximumSize(new Dimension(200, 40));

        // 사운드 토글 버튼 생성
        JToggleButton muteButton = new JToggleButton(util.SoundManager.isMuted() ? "Sound: OFF" : "Sound: ON");
        muteButton.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 15f));
        muteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        muteButton.setMaximumSize(new Dimension(200, 40));
        muteButton.setSelected(util.SoundManager.isMuted()); // 초기 상태 반영

        // 컴포넌트 추가 (여백 포함)
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(title);
        add(Box.createRigidArea(new Dimension(0, 100)));
        add(startButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(difficultyButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(addWordButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(scoreButton);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(exitButton);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(muteButton);
        
        // 게임 시작
        startButton.addActionListener(e -> frame.showGameScreen());

        // 난이도 설정
        difficultyButton.addActionListener(e -> frame.askAndSetDifficulty());

        // 단어 추가 패널 열기
        addWordButton.addActionListener(e -> frame.showEditPanel());

        // 기록 보기
        scoreButton.addActionListener(e -> frame.showTopScores());

        // 나가기
        exitButton.addActionListener(e -> frame.exitGame());

        // 사운드 토글
        muteButton.addActionListener(e -> {
            boolean isMuted = muteButton.isSelected();
            util.SoundManager.setMuted(isMuted);
            muteButton.setText(isMuted ? "Sound: OFF" : "Sound: ON");
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 배경 이미지 그리기
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}