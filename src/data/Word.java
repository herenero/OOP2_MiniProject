package data;

// 게임 내 떨어지는 단어 객체를 표현하는 클래스
// 단어 텍스트와 위치 정보(극좌표계 및 데카르트 좌표계) 관리
public class Word {

    private final String text;

    // 원형 궤도 관련 변수
    private double angle;        // 현재 각도 (라디안)
    private double radius;       // 궤도 중심으로부터의 거리
    private final double angularSpeed; // 궤도를 도는 속도 (각속도)

    // 화면에서의 실제 렌더링 좌표
    private int x;
    private int y;

    public Word(String text, double radius, double angle, double angularSpeed) {
        this.text = text;
        this.radius = radius;
        this.angle = angle;
        this.angularSpeed = angularSpeed;
    }

    // 단어의 위치 업데이트
    // 중심점을 기준으로 궤도를 회전하며 위치 계산
    public void updatePosition(int centerX, int centerY) {
        angle += angularSpeed;
        if (angle > Math.PI * 2) {
            angle -= Math.PI * 2;
        }

        x = centerX + (int) (radius * Math.cos(angle));
        y = centerY + (int) (radius * Math.sin(angle));
    }

    // 단어의 궤도 반지름을 줄여서 중심으로 접근시킴
    public void decreaseRadius(double amount) {
        this.radius -= amount;
        if (this.radius < 0) {
            this.radius = 0;
        }
    }

    public double getRadius() {
        return radius;
    }

    public String getText() {
        return text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}