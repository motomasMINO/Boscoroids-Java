import java.awt.image.BufferedImage;

// アニメーションを管理するクラス
public class Animation {

    private BufferedImage[] frames;
    private int velocity;
    private int index;
    private boolean running;
    private Vector2D position;
    private long time;

    public Animation(BufferedImage[] frames, int velocity, Vector2D position) {
        this.frames = frames; // アニメーションのフレームを格納する配列
        this.velocity = velocity; // フレームの切り替え速度（ミリ秒単位）
        this.position = position; // アニメーションの位置
        index = 0;
        running = true; // アニメーションが再生中かどうかを示すフラグ
    }

    // アニメーションの更新
    public void update(float dt) {
        time += dt; // 経過時間を更新

        // 経過時間がフレームの切り替え速度を超えたら、次のフレームに切り替える
        if(time > velocity) {
          time = 0;
          index ++;
          if(index >= frames.length) { // 最後のフレームまで再生したらアニメーションを停止
            running = false;
            index = 0;
          }  
        }
    }

    // アニメーションが再生中かどうかを返す
    public boolean isRunning() {
        return running;
    }

    // アニメーションの位置を取得するためのゲッター
    public Vector2D getPosition() {
        return position;
    }

    // 現在のフレームを取得するためのゲッター
    public BufferedImage getCurrentFrame() {
        return frames[index];
    }
}
