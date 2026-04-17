import java.awt.*;
import java.awt.image.BufferedImage;

// ゲーム内のオブジェクトを表す抽象クラス
public abstract class GameObject {
    protected BufferedImage texture;
    protected Vector2D position;

    public GameObject(Vector2D position, BufferedImage texture) {
        this.position = position;
        this.texture = texture;
    }

    // ゲームオブジェクトの更新
    public abstract void update(float dt);

    // ゲームオブジェクトの描画
    public abstract void draw(Graphics g);

    // ゲームオブジェクトの位置を取得・設定するためのゲッターとセッター
    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

}
