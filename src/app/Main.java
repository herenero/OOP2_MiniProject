package app;

// 애플리케이션의 진입점 클래스
public class Main {

    // 게임 창의 기본 너비와 높이
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static void main(String[] args) {
        // 게임 프레임 생성 및 시작
        new GameFrame();
    }
}