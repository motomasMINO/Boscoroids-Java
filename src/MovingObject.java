import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

// ゲーム内で移動するオブジェクトの抽象クラス
public abstract class MovingObject extends GameObject {

    protected Vector2D velocity; // オブジェクトの速度を管理するための変数
    protected AffineTransform at; // オブジェクトの回転を管理するための変数
    protected double angle; // オブジェクトの回転角度を管理するための変数
    protected double maxVel; // オブジェクトの最大速度
    protected int width;
    protected int height;
    protected GameState gameState;


    protected boolean Dead; // オブジェクトが破壊されたかどうかを示すフラグ

    public MovingObject(Vector2D position, Vector2D velocity, double maxVel, BufferedImage texture, GameState gameState) {
        super(position, texture); // GameObjectクラスのコンストラクタを呼び出して位置とテクスチャを初期化
        this.velocity = velocity;
        this.maxVel = maxVel;
        this.gameState = gameState;
        width = texture.getWidth();
        height = texture.getHeight();
        angle = 0;
        Dead = false;
    }

    // オブジェクト同士の衝突(当たり判定)を処理するためのメソッド
    protected void collidesWith() {
        ArrayList<MovingObject> movingObjects = gameState.getMovingObjects();

        // オブジェクト同士の衝突を処理するためのループ
        for(int i = 0; i < movingObjects.size(); i++) {

           MovingObject m = movingObjects.get(i);
           
           // 自分自身との衝突は無視する
           if(m.equals(this))
             continue;

           double distance = m.getCenter().subtract(getCenter()).getMagnitude(); // mと自分の中心座標の距離を計算する
           
           // もし距離がmの半径と自分の半径の和より小さい場合、つまり衝突している場合で、両方とも破壊されていない場合は、衝突処理を行う
           if(distance < m.width/2 + width/2 && movingObjects.contains(this) && !m.Dead && !Dead) {
             objectCollision(this, m);
           }
        }
    }

    private void objectCollision(MovingObject a, MovingObject b) {
        Player p = null;

        // プレイヤー同士の衝突や、隕石同士の衝突は無視する
        if(a instanceof Player) // もしaがPlayerのインスタンスであれば、pにaをキャストして代入
           p = (Player)a;
        else if(b instanceof Player) // もしbがPlayerのインスタンスであれば、pにbをキャストして代入
                p = (Player)b;
                
        if(p !=null && p.isSpawning()) // もしpがスポーン中であれば、衝突を無視する
           return;        
      
        if(a instanceof Meteor && b instanceof Meteor) // もしaとbの両方がMeteorのインスタンスであれば、衝突を無視する
           return;

        if(!(a instanceof PowerUp || b instanceof PowerUp)) { // もしaとbのどちらもPowerUpのインスタンスでなければ、両方を破壊する
             a.Destroy();
             b.Destroy();
             return;
        }

        if(p != null) { // もしpがnullでなければ、つまりaかbのどちらかがPlayerのインスタンスであれば、PowerUpを実行して破壊する
           if(a instanceof Player) {
              ((PowerUp)b).executeAction();
              b.Destroy();
           }else if(b instanceof Player) { // もしbがPlayerのインスタンスであれば、aをPowerUpとして実行して破壊する
                   ((PowerUp)a).executeAction();
                   a.Destroy();
           }
        }
    }

    // オブジェクトが破壊されたときの処理を行うためのメソッド
    protected void Destroy() {
        Dead = true;
    }

    // オブジェクトの中心座標を取得するためのメソッド
    protected Vector2D getCenter() {
        return new Vector2D(position.getX() + width/2, position.getY() + height/2);
    }

    // オブジェクトが破壊されたかどうかを返すためのメソッド
    public boolean isDead() {
      return Dead;
    }
}