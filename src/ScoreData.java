import java.text.SimpleDateFormat;
import java.util.Date;

// スコアデータを管理するクラス
public class ScoreData {
    private String date;
    private int score;

    public ScoreData(int score) {
        this.score = score;

        Date today = new Date(System.currentTimeMillis()); // 現在の日付を取得
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // 日付のフォーマットを指定
        date = format.format(today);
    }

    // デフォルトコンストラクタ（引数なし）も用意しておく
    public ScoreData() {}

    // ゲッターとセッター
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    
}
