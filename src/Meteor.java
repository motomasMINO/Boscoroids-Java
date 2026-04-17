import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

// 隕石用のクラス
public class Meteor extends MovingObject {

    private Size size;
    private Sound explosion;

    public Meteor(Vector2D position, Vector2D velocity, double maxVel, BufferedImage texture, GameState gameState, Size size) {
        super(position, velocity, maxVel, texture, gameState); // MovingObjectクラスのコンストラクタを呼び出す
        this.size = size;
        this.velocity = velocity.scale(maxVel);
        explosion = new Sound(Assets.explosion);
    }

    // 隕石の更新
    @Override
    public void update(float dt) {
        Vector2D playerPos = new Vector2D(gameState.getPlayer().getCenter()); // プレイヤーの位置を取得

        int distanceToPlayer = (int) playerPos.subtract(getCenter()).getMagnitude(); // プレイヤーとの距離を計算

        // プレイヤーがシールドを持っている場合、距離が一定以下ならば隕石はプレイヤーから逃げるように動く
        if(distanceToPlayer < Constants.SHIELD_DISTANCE / 2 + width /2) {
          if(gameState.getPlayer().isShieldOn()) {
            Vector2D fleeForce = fleeForce();
            velocity = velocity.add(fleeForce);
          }
        }

        // 隕石の速度が最大速度を超えないようにする
        if(velocity.getMagnitude() >= this.maxVel) { // 隕石の速度が最大速度を超えた場合、減速する
          Vector2D reversedVelocity = new Vector2D(-velocity.getX(), -velocity.getY());
          velocity = velocity.add(reversedVelocity.normalize().scale(0.01f));
        }

      velocity = velocity.limit(Constants.METEOR_MAX_VEL); // 隕石の速度を最大速度に制限する

      position = position.add(velocity);

      // 隕石が画面の端を超えた場合、反対側に出現させる
      if(position.getX() > Constants.WIDTH)
        position.setX(-width);
      if(position.getY() > Constants.HEIGHT)
        position.setY(-height);

      if(position.getX() < -width)
        position.setX(Constants.WIDTH);
      if(position.getY() < -height)
        position.setY(Constants.HEIGHT);

      angle += Constants.DELTAANGLE / 2;  // 隕石を回転させる
    }

    // プレイヤーから逃げるための力を計算するメソッド
    private Vector2D fleeForce() {
        Vector2D desiredVelocity = gameState.getPlayer().getCenter().subtract(getCenter()); // プレイヤーの位置から隕石の位置へのベクトルを計算
        desiredVelocity = (desiredVelocity.normalize()).scale(Constants.METEOR_MAX_VEL);
        Vector2D v = new Vector2D(velocity);
        return v.subtract(desiredVelocity);
    }

    // 隕石が破壊されたときの処理
    @Override
    public void Destroy() {
        gameState.divideMeteor(this);
        gameState.playExplosion(position);
        gameState.addScore(Constants.METEOR_SCORE, position);
        super.Destroy();
        explosion.play();
    }

    // 隕石の描画
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        
        at = AffineTransform.getTranslateInstance(position.getX(), position.getY()); // 隕石の位置に合わせて画像を移動させる

        at.rotate(angle, width / 2, height / 2); // 隕石の中心を軸にして回転させる

        g2d.drawImage(texture, at, null);
    }

    // 隕石のサイズを取得するメソッド
    public Size getSize() {
        return size;
    }
    
}
