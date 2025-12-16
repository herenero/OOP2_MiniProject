package app;

import controller.GameController;
import difficulty.Difficulty;
import repository.FileScoreRepository;
import repository.FileTextRepository;
import repository.interfaces.ScoreRepository;
import repository.interfaces.TextRepository;
import ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// 게임의 메인 프레임(창)을 담당하는 클래스
// 화면 전환 및 전체적인 UI 구성 관리
public class GameFrame extends JFrame {

    private final TextRepository textRepository;
    private final ScoreRepository scoreRepository;
    private final ScorePanel scorePanel;
    private final GameController controller;
    private final GamePanel gamePanel;
    private final EditPanel editPanel;
    private final ScoreHistoryPanel scoreHistoryPanel; // 점수 기록 패널
    private final StartMenuPanel startMenuPanel;

    // 현재 선택된 게임 난이도
    private Difficulty selectedDifficulty = Difficulty.EASY;

    private JPanel mainContainer;  // 화면 전환을 위한 패널 컨테이너
    private JToolBar toolBar; // 게임 플레이 시 표시되는 상단 툴바

    public GameFrame() {
        super("별똥별 사냥꾼");

        // 리포지토리 및 컨트롤러 초기화. 의존성 주입.
        this.textRepository = new FileTextRepository("words.txt");
        this.scoreRepository = new FileScoreRepository("scores.txt");
        this.scorePanel = new ScorePanel();
        this.controller = new GameController(textRepository, scoreRepository, scorePanel);
        this.controller.setOnGameOver(this::showStartMenu); // 게임 오버 시 시작 메뉴로 자동 전환

        // UI 패널 초기화. 컨트롤러를 주입하여 패널들이 게임 로직과 상호작용하도록 함
        this.gamePanel = new GamePanel(controller);
        this.editPanel = new EditPanel(controller);
        this.scoreHistoryPanel = new ScoreHistoryPanel(scoreRepository);
        this.startMenuPanel = new StartMenuPanel(this);

        setSize(Main.WIDTH, Main.HEIGHT); // 프레임 초기 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 버튼 클릭 시 프로그램 종료

        // ESC 키를 눌렀을 때 게임 중이면 메인 메뉴로 돌아가도록 설정 (KeyBinding)
        // InputMap/ActionMap은 JFrame 전체에 적용되는 키 이벤트를 처리하는 표준 방식
        JRootPane rootPane = this.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "returnToMenu");
        actionMap.put("returnToMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller.isRunning()) {
                    returnToStartMenu();
                }
            }
        });

        // 상단 툴바 생성 및 초기 설정
        createToolBar();
        
        mainContainer = new JPanel(new BorderLayout());
        
        // 프레임의 전체 레이아웃 설정: 툴바는 상단, 메인 콘텐츠는 중앙
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(mainContainer, BorderLayout.CENTER);

        // 프로그램 시작 시 초기 화면은 시작 메뉴로 설정
        showStartMenu();

        setLocationRelativeTo(null); // 화면 중앙에 프레임 배치
        setVisible(true); // 프레임을 보이게 함

        // 배경음악 재생 시작
        util.SoundManager.playBGM("background.wav");
    }

    // 게임 플레이 중 표시되는 툴바를 생성하고 구성
    private void createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false); // 툴바 이동 불가 설정
        toolBar.setVisible(false); // 게임 시작 전에는 툴바를 숨김

        JButton pauseButton = new JButton("일시정지");
        pauseButton.addActionListener(e -> {
            controller.togglePause();
            if (controller.isPaused()) {
                pauseButton.setText("재개");
            } else {
                pauseButton.setText("일시정지");
            }
        });
        toolBar.add(pauseButton);

        JButton exitButton = new JButton("나가기");
        // '나가기' 버튼 클릭 시 게임 중지 후 메인 메뉴로 돌아감
        exitButton.addActionListener(e -> returnToStartMenu());
        toolBar.add(exitButton);
    }

    // 시작 메뉴 화면을 보여주는 메서드
    public void showStartMenu() {
        if (toolBar != null) {
            toolBar.setVisible(false); // 시작 메뉴에서는 툴바를 숨김
        }
        mainContainer.removeAll(); // 기존 콘텐츠 제거
        mainContainer.add(startMenuPanel, BorderLayout.CENTER); // 시작 메뉴 패널 추가
        mainContainer.revalidate(); // 레이아웃 재계산
        mainContainer.repaint(); // 다시 그리기
    }

    // 게임 플레이 화면을 보여주고 게임을 시작하는 메서드
    // 좌측에는 게임 플레이 영역, 우측에는 점수 및 기록 영역을 배치
    public void showGameScreen() {
        if (toolBar != null) {
            toolBar.setVisible(true); // 게임 화면에서는 툴바를 표시
        }
        mainContainer.removeAll(); // 기존 콘텐츠 제거
        
        scoreHistoryPanel.refreshScores(); // 게임 시작 시 점수 기록 목록을 갱신

        // 우측 영역을 점수판(상단)과 기록 패널(하단)으로 분할
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setTopComponent(scorePanel); // 상단에 현재 점수판
        rightSplitPane.setBottomComponent(scoreHistoryPanel); // 하단에 점수 기록 패널
        rightSplitPane.setDividerLocation(150); // 우측 패널의 초기 분할 위치
        rightSplitPane.setResizeWeight(0.2); // 우측 패널 리사이즈 시 상단 점수판이 작게 유지

        // 전체 화면을 좌측(게임)과 우측(점수/기록)으로 분할
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setLeftComponent(gamePanel); // 좌측에 게임 플레이 패널
        mainSplitPane.setRightComponent(rightSplitPane); // 우측에 점수/기록 분할 패널
        mainSplitPane.setDividerLocation(600); // 전체 분할기의 초기 위치 (게임 화면을 넓게)
        mainSplitPane.setResizeWeight(0.85); // 리사이즈 시 게임 화면이 더 넓게 유지

        mainContainer.add(mainSplitPane, BorderLayout.CENTER); // 분할된 화면을 메인 컨테이너에 추가

        // 게임 컨트롤러에게 게임 시작을 알림
        controller.startGame(gamePanel.getGroundPanel(), selectedDifficulty);

        mainContainer.revalidate();
        mainContainer.repaint();
    }

    // 단어 추가/수정 화면을 보여주는 메서드
    // 현재는 게임 중에는 우측 패널에 포함되지 않고, 별도 메뉴에서 호출될 때 사용
    public void showEditPanel() {
        mainContainer.removeAll();
        mainContainer.add(editPanel, BorderLayout.CENTER);

        // 뒤로가기 버튼 추가 (단어 추가 화면에서 시작 메뉴로 돌아가기 위함)
        JButton back = new JButton("뒤로가기");
        back.addActionListener(e -> showStartMenu());
        JPanel south = new JPanel(new FlowLayout());
        south.add(back);

        mainContainer.add(south, BorderLayout.SOUTH);

        mainContainer.revalidate();
        mainContainer.repaint();
    }

    // 난이도 설정 다이얼로그를 띄워 사용자로부터 난이도를 입력받고 저장
    public void askAndSetDifficulty() {
        String[] options = {"Easy", "Normal", "Hard"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "난이도를 선택하세요.",
                "난이도 설정",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (choice == 0) {
            selectedDifficulty = Difficulty.EASY;
        } else if (choice == 1) {
            selectedDifficulty = Difficulty.NORMAL;
        } else if (choice == 2) {
            selectedDifficulty = Difficulty.HARD;
        }

        JOptionPane.showMessageDialog(
                this,
                "난이도가 " + selectedDifficulty + "로 설정되었습니다.",
                "알림",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // 상위 점수 목록을 다이얼로그로 보여주는 메서드
    public void showTopScores() {
        String msg = controller.getTopScoresText(10); // 컨트롤러를 통해 Top 10 점수 가져옴
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Top 10 Scores",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // 게임 도중 ESC 키 또는 '나가기' 버튼 클릭 시 메인 메뉴로 돌아가는 처리
    public void returnToStartMenu() {
        if (controller.isRunning()) { // 게임이 진행 중일 때만 처리
            controller.stopGame(); // 게임 루프 중지
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "메인 화면으로 돌아갑니다. 점수를 저장하시겠습니까?",
                    "게임 중지",
                    JOptionPane.YES_NO_OPTION
            );

            if (answer == JOptionPane.YES_OPTION) {
                controller.saveScoreWithDialog(); // 점수 저장 다이얼로그 호출
            }
        }
        showStartMenu(); // 시작 메뉴 화면 표시
    }
    
    // 애플리케이션 종료 시 호출되는 메서드
    public void exitGame() {
        if (controller.isRunning()) { // 게임이 진행 중이면 점수 저장 여부 확인
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "게임이 진행 중입니다. 점수를 저장하시겠습니까?",
                    "종료",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );

            if (answer == JOptionPane.CANCEL_OPTION) // '취소'를 누르면 종료하지 않음
                return;

            if (answer == JOptionPane.YES_OPTION) {
                controller.saveScoreWithDialog(); // 점수 저장 다이얼로그 호출
            }
        }
        dispose(); // 프레임 자원 해제
        System.exit(0); // 프로그램 강제 종료
    }
}