package data;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private final String playerName;
    private final int score;

    public ScoreEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(ScoreEntry o) {
        // 내림차순 정렬
        return Integer.compare(o.score, this.score);
    }
}
