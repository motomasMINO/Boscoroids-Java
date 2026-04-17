import java.awt.Color;
import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

// スコア表示画面のクラス
public class ScoreState extends State {

    private Button returnButton;

    private PriorityQueue<ScoreData> highScores; // スコアデータを格納する優先度付きキュー

    private Comparator<ScoreData> scoreComparator; // スコアを比較するためのComparator

    private ScoreData[] auxArray; // PriorityQueueを配列に変換してソートするための補助配列

    public ScoreState() {
        returnButton = new Button(Assets.greyBtn, Assets.blueBtn, Assets.greyBtn.getHeight(),
                                  Constants.HEIGHT - Assets.greyBtn.getHeight() * 2, Constants.RETURN,
                       new Action() {
                               @Override
                               public void doAction() {
                                   State.changeState(new MenuState()); // メニュー画面に遷移
                               }
                       });

       scoreComparator = new Comparator<ScoreData>() {
        @Override
        public int compare(ScoreData e1, ScoreData e2) { // スコアを比較して順位を決定する
            // スコアが小さいほど順位が高くなる
            return e1.getScore() < e2.getScore() ? -1: e1.getScore() > e2.getScore() ? 1: 0; 
        }
       };

       // スコアデータを格納する優先度付きキューを初期化
       highScores = new PriorityQueue<ScoreData>(10, scoreComparator);

       // JSONファイルからスコアデータを読み込む
       try {
        ArrayList<ScoreData> dataList = JSONParser.readFile();

        for(ScoreData d: dataList) { // 読み込んだスコアデータを優先度付きキューに追加
            highScores.add(d);
        }

        while(highScores.size() > 10) { // 上位10件のスコアのみを保持する
            highScores.poll();
        }

    }catch(FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}

    @Override
    public void update(float dt) {
        returnButton.update();
    }

    // スコア表示画面を描画するメソッド
    @Override
    public void draw(Graphics g) {
        returnButton.draw(g);
        auxArray = highScores.toArray(new ScoreData[highScores.size()]); // 優先度付きキューを配列に変換
        Arrays.sort(auxArray, scoreComparator); // 優先度付きキューを配列に変換してスコアの順番でソート

        Vector2D scorePos = new Vector2D(Constants.WIDTH / 2 - 200, 100); // スコアの表示位置を設定
        Vector2D datePos = new Vector2D(Constants.WIDTH / 2 + 200, 100); // 日付の表示位置を設定

        Text.drawText(g, Constants.SCORE, scorePos, true, Color.BLUE, Assets.fontBig);
        Text.drawText(g, Constants.DATE, datePos, true, Color.BLUE, Assets.fontBig);

        scorePos.setY(scorePos.getY() + 40);
        datePos.setY(datePos.getY() + 40);

        // スコアデータをスコアの順番で描画
        for(int i = auxArray.length - 1; i > -1; i--) {
            ScoreData d = auxArray[i];

            Text.drawText(g, Integer.toString(d.getScore()), scorePos, true, Color.WHITE, Assets.fontMed);
            Text.drawText(g, d.getDate(), datePos, true, Color.WHITE, Assets.fontMed);

            scorePos.setY(scorePos.getY() + 40);
            datePos.setY(datePos.getY() + 40);
        }
    }
}