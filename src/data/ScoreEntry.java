package data;

// 게임 점수 정보를 저장하는 불변 클래스
// 플레이어 이름과 점수를 포함하며, 점수 비교를 위해 Comparable 구현
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
    public int compareTo(ScoreEntry other) {
        // 점수 내림차순 정렬을 위해 비교
        return Integer.compare(other.score, this.score);
    }
}