import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

// UFO用のクラス
public class Ufo extends MovingObject {
    
    private ArrayList<Vector2D> path;

    private Vector2D currentNode;

    private int index;

    private boolean following;

    private long fireRate;

    private Sound ufoDestroy;

    public Ufo(Vector2D position, Vector2D velocity, double maxVel, BufferedImage texture, 
               ArrayList<Vector2D> path, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState); // MovingObjectのコンストラクタを呼び出す
        this.path = path;
        index = 0;
        following = true;
        fireRate = 0;
        ufoDestroy = new Sound(Assets.ufoDestroy);
    }

    // UFOがパスに沿って移動するためのメソッド
    private Vector2D pathFollowing() {
        currentNode = path.get(index);
        
        double distanceToNode = currentNode.subtract(getCenter()).getMagnitude(); // 現在の位置と次のノードとの距離を計算

        if(distanceToNode < Constants.NODE_RADIUS) { // ノードに近づいたら次のノードに切り替える
          index ++;
          if(index >= path.size()) { // パスの最後に到達したら移動を停止する
            following = false;
          }  
        }

        return seekForce(currentNode); // 次のノードに向かうための力を計算して返す
    }

    // 次のノードに向かうための力を計算するメソッド
    private Vector2D seekForce(Vector2D target) {
        Vector2D desiredVelocity = target.subtract(getCenter()); // 現在の位置と目標位置とのベクトルを計算
        desiredVelocity = desiredVelocity.normalize().scale(maxVel); // 目標位置に向かう単位ベクトルを最大速度にスケーリング
        return desiredVelocity.subtract(velocity);
    }

    // UFOの更新処理
    @Override
    public void update(float dt) {
        fireRate += dt;

        Vector2D pathFollowing;

        if(following) // パスに沿って移動する場合、pathFollowing()メソッドを呼び出して次のノードに向かうための力を計算する
          pathFollowing = pathFollowing();
        else // パスに沿って移動しない場合、力はゼロになる
          pathFollowing = new Vector2D();
          
        pathFollowing = pathFollowing.scale(1 / Constants.UFO_MASS);   // 質量を考慮して力を加速度に変換する

        velocity = velocity.add(pathFollowing);

        velocity = velocity.limit(maxVel);

        position = position.add(velocity);

        // UFOが画面外に出たら破壊する
        if(position.getX() > Constants.WIDTH || position.getY() > Constants.HEIGHT || position.getX() < -width || position.getY() < -height)
          Destroy();

        // UFOの攻撃
        if(fireRate > Constants.UFO_FIRE_RATE) { // 一定時間ごとに攻撃する
          Vector2D toPlayer = gameState.getPlayer().getCenter().subtract(getCenter());

          toPlayer = toPlayer.normalize();

          double currentAngle = toPlayer.getAngle();

          // UFOの攻撃にランダムな角度のばらつきを加える
          currentAngle += Math.random() * Constants.UFO_ANGLE_RANGE - Constants.UFO_ANGLE_RANGE / 2;

          if(toPlayer.getX() < 0) // プレイヤーがUFOの左側にいる場合、レーザーを反転させる
            currentAngle = -currentAngle + Math.PI;

          toPlayer = toPlayer.setDirection(currentAngle);

          // UFOの中心からプレイヤーに向かってレーザーを発射する
          Laser laser = new Laser(getCenter().add(toPlayer.scale(width)), toPlayer, Constants.UFO_LASER_VEL, currentAngle + Math.PI/2, Assets.redLaser, gameState);

          gameState.getMovingObjects().add(0, laser);

          fireRate = 0;
        }  
        
        collidesWith();
    }

    // UFOが破壊されたときの処理
    public void Destroy() {
        gameState.addScore(Constants.UFO_SCORE, position);
        gameState.playExplosion(position);
        super.Destroy();
        ufoDestroy.play();
    }

    // UFOの描画処理
    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        // UFOの向きを速度ベクトルの方向に合わせるためのアフィン変換を作成する
        at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
        
        at.rotate(angle, width/2, height/2); // UFOの中心を回転の中心にするために、幅と高さの半分を指定する

        g2d.drawImage(texture, at, null);
    }
      
}
