import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

// ボタンを管理するクラス(マウスオーバーとクリックの処理を含む)
public class Button {
    
    private BufferedImage mouseOutImg;
    private BufferedImage mouseInImg;
    private boolean mouseIn;
    private Rectangle boundingBox;
    private Action action;
    private String text;

    public Button(BufferedImage mouseOutImg, BufferedImage mouseInImg, int x, int y, String text, Action action) {
        this.mouseInImg = mouseInImg; // マウスオーバー時の画像
        this.mouseOutImg = mouseOutImg; // マウスオーバーしていないときの画像
        this.text = text; // ボタンに表示するテキスト
        boundingBox = new Rectangle(x, y, mouseInImg.getWidth(), mouseInImg.getHeight()); // ボタンの位置とサイズを定義する矩形
        this.action = action; // ボタンがクリックされたときに実行されるアクションを定義するインターフェース
    }

    // ボタンの更新
    public void update() {
        if(boundingBox.contains(MouseInput.X, MouseInput.Y)) { // マウスの位置がボタンの矩形内にあるかどうかをチェック
            mouseIn = true;
        }else {
            mouseIn = false; // マウスの位置がボタンの矩形内にない場合は、マウスオーバーしていない状態にする
        }

        if(mouseIn && MouseInput.MLB) { // マウスオーバーしている状態で、マウスの左ボタンが押された場合にアクションを実行する
            action.doAction();
        }
    }

    // ボタンの描画
    public void draw(Graphics g) {
        if(mouseIn) { // マウスオーバーしている状態であれば、マウスオーバー用の画像を描画する
            g.drawImage(mouseInImg, boundingBox.x, boundingBox.y, null);
        }else {
            g.drawImage(mouseOutImg, boundingBox.x, boundingBox.y, null); // マウスオーバーしていない状態であれば、通常の画像を描画する
        }

        // ボタンのテキストを描画するために、TextクラスのdrawTextメソッドを呼び出す。
        // テキストはボタンの中央に配置されるように、boundingBoxの中心座標を計算して渡す。
        Text.drawText(g, text, new Vector2D(boundingBox.getX() + boundingBox.getWidth() / 2, 
                                            boundingBox.getY() + boundingBox.getHeight()), true, Color.BLACK, Assets.fontMed);
    }
}
