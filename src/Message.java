import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

// ゲーム内のメッセージを管理するクラス
public class Message {
    private float alpha; // メッセージの透明度
    private String text;
    private Vector2D position;
    private Color color;
    private boolean center;
    private boolean fade; // メッセージがフェードアウトするかどうかを示すフラグ
    private Font font;
    private final float deltaAlpha = 0.01f; // 透明度の変化量
    private boolean dead; // メッセージが消えるべきかどうかを示すフラグ

    public Message(Vector2D position, boolean fade, String text, Color color, boolean center, Font font) {
        this.font = font;
        this.text = text;
        this.position = new Vector2D(position);
        this.fade = fade;
        this.color = color;
        this.center = center;
        this.dead = false;

        if(fade) // フェードアウトする場合は最初は完全に表示されるようにする
          alpha = 1;
        else // フェードインする場合は最初は完全に透明にする
          alpha = 0;  
    }

    // メッセージを描画するメソッド
    public void draw(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)); // 現在の透明度を設定

        Text.drawText(g2d, text, position, center, color, font);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1)); // 透明度を元に戻す

        position.setY(position.getY() - 1); // メッセージが上に移動するようにする

        if(fade) // フェードアウトする場合
          alpha -= deltaAlpha;
        else
          alpha += deltaAlpha;
          
        // フェードアウトが完了したらメッセージを消す
        if(fade && alpha < 0) {
          dead = true;
        }
        
        // フェードインが完了したらフェードアウトに切り替える
        if(!fade && alpha > 1) {
          fade = true;
          alpha = 1;
        }
    }

    // メッセージが消えるべきかどうかを返すメソッド
    public boolean isDead() {
        return dead;
    }
}
