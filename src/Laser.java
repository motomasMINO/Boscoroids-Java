import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

// レーザーを表すクラス
public class Laser extends MovingObject {

    // レーザーの角度
    public Laser(Vector2D position, Vector2D velocity, double maxVel, double angle, BufferedImage texture, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState);
        this.angle = angle;
        this.velocity = velocity.scale(maxVel);
    }

    // レーザーが画面外に出たときや、他のオブジェクトと衝突したときに呼び出されるメソッド
    @Override
    public void update(float dt) {
        position = position.add(velocity); // レーザーの位置を更新
        // レーザーが画面外に出た場合は削除する
        if(position.getX() < 0 || position.getX() > Constants.WIDTH || position.getY() < 0 || position.getY() > Constants.HEIGHT) {
          Destroy();
        }

        collidesWith();
    }

    // レーザーの描画
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        at = AffineTransform.getTranslateInstance(position.getX() - width/2, position.getY()); // レーザーの中心を基準に回転させる

        at.rotate(angle, width/2, 0); // レーザーの角度に合わせて回転させる

        g2d.drawImage(texture, at, null);
    }
    
    // レーザーの中心を取得するメソッド
    @Override
    public Vector2D getCenter() {
        return new Vector2D(position.getX() + width/2, position.getY() + width/2);
    }
}
