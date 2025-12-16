package ui;

import controller.GameController;
import data.Word;
import util.ImageLoader;

import javax.swing.JPanel;
import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;

// 실제 게임이 진행되는 그래픽 영역 패널
// 배경, 플레이어(행성), 떨어지는 단어(별똥별) 등을 그림
public class GroundPanel extends JPanel {

    private final GameController controller;
    private final BufferedImage backgroundImage; // 게임 배경 이미지
    private final BufferedImage playerImage;     // 플레이어(행성) 이미지
    private final BufferedImage starImage;       // 떨어지는 단어(별똥별) 이미지
    
    private boolean isPaused = false; // 일시정지 상태

    public GroundPanel(GameController controller) {
        this.controller = controller;
        // 이미지 리소스 로드
        this.backgroundImage = ImageLoader.getImage("background_game.jpg");
        this.playerImage = ImageLoader.getImage("planet.png");
        this.starImage = ImageLoader.getImage("star.png");
        
        setBackground(Color.BLACK); // 배경색 기본 설정
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // 컴포넌트의 기본 그리기 기능을 호출하여 배경을 지움

        // Graphics 객체를 Graphics2D로 캐스팅하여 고급 2D 그래픽 기능을 사용
        Graphics2D g2d = (Graphics2D) g;
        // 텍스트 렌더링 품질 향상 (안티앨리어싱 적용)
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        int width = getWidth();  // 패널의 현재 너비
        int height = getHeight(); // 패널의 현재 높이

        // 배경 그리기: 배경 이미지가 있으면 이미지를 있고, 없으면 검정색으로 채움
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, width, height, this);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, width, height);
        }

        // 플레이어(행성) 그리기 (화면 중앙에 배치)
        int playerRadius = (int)(Math.min(width, height) * 0.12); // 화면 크기에 비례하여 플레이어 크기 결정
        int centerX = width / 2;  // 중앙 X 좌표
        int centerY = height / 2; // 중앙 Y 좌표
        int playerSize = playerRadius * 2; // 플레이어 이미지의 지름

        if (playerImage != null) {
            // 이미지의 중심이 centerX, centerY에 오도록 위치 조정하여 그림
            g2d.drawImage(playerImage, centerX - playerRadius, centerY - playerRadius, playerSize, playerSize, this);
        } else {
            g2d.setColor(Color.GREEN);
            g2d.fillOval(centerX - playerRadius, centerY - playerRadius, playerSize, playerSize);
        }

        // 별똥별(단어) 그리기
        List<Word> words = controller.getActiveWordsSnapshot(); // 컨트롤러로부터 현재 활성화된 단어 목록을 가져옴
        
        for (Word word : words) {
            int x = word.getX(); // 단어의 현재 X 좌표
            int y = word.getY(); // 단어의 현재 Y 좌표
            
            // 별똥별 이미지 그리기
            if (starImage != null) {
                 g2d.drawImage(starImage, x - 20, y - 20, 40, 40, this); // 이미지 중심이 x,y에 오도록 위치 조정
            } else {
                g2d.setColor(Color.YELLOW);
                g2d.fillOval(x - 12, y - 12, 24, 24); // 이미지가 없을 경우 노란색 원으로 대체
            }

            // 단어 텍스트 그리기
            g2d.setColor(Color.WHITE); // 텍스트 색상
            // 폰트 적용 (Bold 스타일, 14f 크기)
            g2d.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 14f).deriveFont(Font.BOLD));
            
            // FontMetrics를 사용하여 텍스트의 실제 크기를 측정하고, 중앙에 배치
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(word.getText()); // 텍스트의 픽셀 너비
            // x 좌표에서 텍스트 너비의 절반만큼 왼쪽으로 이동하여 중앙 정렬. y + 30은 이미지 아래에 텍스트를 배치하기 위함
            g2d.drawString(word.getText(), x - (textWidth / 2), y + 30);
        }

        // 충돌 계산을 위해 컨트롤러에 현재 플레이어(행성)의 중심값과 반경을 전달
        // 이 정보는 GameController에서 단어와의 충돌 여부를 판단하는 데 사용
        controller.setOrbitCenter(centerX, centerY);
        controller.setPlayerCollisionRadius(playerRadius);

        // 5. 일시정지 오버레이
        if (isPaused) {
            g2d.setColor(new Color(0, 0, 0, 150)); // 반투명 검정 배경
            g2d.fillRect(0, 0, width, height);

            g2d.setColor(Color.WHITE);
            g2d.setFont(util.FontLoader.getFont("Pretendard-Regular.ttf", 40f).deriveFont(Font.BOLD));
            String pauseText = "PAUSED";
            FontMetrics fm = g2d.getFontMetrics();
            int textW = fm.stringWidth(pauseText);
            int textH = fm.getAscent();
            g2d.drawString(pauseText, (width - textW) / 2, (height + textH) / 2);
        }
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        repaint();
    }
}