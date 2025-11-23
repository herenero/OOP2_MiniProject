package data;

public class Word {
    private final String word;
    private int x, y;
    private final int speed;

    public Word(String word, int x, int y, int speed) {
        this.word = word;
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public String getWord() {
        return word;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveDown() {
        y += speed;
    }
}
