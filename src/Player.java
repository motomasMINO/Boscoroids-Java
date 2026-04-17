import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

// プレイヤー用のクラス
public class Player extends MovingObject {

    private Vector2D heading; // プレイヤーの向きを表すベクトル
    private Vector2D acceleration;

    private boolean accelerating = false; // プレイヤーが加速しているかどうかを示すフラグ
    private long fireRate; // レーザーの発射間隔を管理するための変数

    private boolean spawning, visible; // スポーン中かどうか、表示されるかどうかを示すフラグ

    private long spawnTime, flickerTime, shieldTime, doubleScoreTime, rapidFireTime, doubleGunTime;

    private Sound shoot, lose, ACC;

    private boolean shieldOn, doubleScoreOn, rapidFireOn, doubleGunOn;

    private Animation shieldEffect;

    private long fireSpeed;

    public Player(Vector2D position, Vector2D velocity, double maxVel, BufferedImage texture, GameState gameState) {
        super(position, velocity, maxVel, texture, gameState); // MovingObjectクラスのコンストラクタを呼び出す
        heading = new Vector2D(0, 1); // プレイヤーの初期の向きを下向きに設定
        acceleration = new Vector2D();
        fireRate = 0;
        spawnTime = 0;
        flickerTime = 0;
        shieldTime = 0;
        rapidFireTime = 0;
        doubleGunTime = 0;
        shoot = new Sound(Assets.playerShoot);
        lose = new Sound(Assets.playerLose);
        ACC = new Sound(Assets.accelerationMusic);

        shieldEffect = new Animation(Assets.shieldEffect, 80, null);

        visible = true;
    }

    // プレイヤーの更新処理
    @Override
    public void update(float dt) {
        fireRate += dt; // レーザーの発射間隔を更新

        if(shieldOn) // シールドがオンの場合、シールドの持続時間を更新
          shieldTime += dt;

        if(doubleScoreOn) // ダブルスコアがオンの場合、ダブルスコアの持続時間を更新
          doubleScoreTime += dt;
          
        if(rapidFireOn) { // 連射弾がオンの場合、ラピッドファイアの持続時間を更新し、発射間隔を短くする
          fireSpeed = Constants.FIRERATE / 2;
          rapidFireTime += dt;
        }else { // 連射弾がオフの場合、発射間隔を通常に戻す
          fireSpeed = Constants.FIRERATE;
        }
        
        if(doubleGunOn) // ダブルガンがオンの場合、ダブルガンの持続時間を更新
          doubleGunTime += dt;

        if(shieldTime > Constants.SHIELD_TIME) { // シールドの持続時間が一定時間を超えた場合、シールドをオフにする
          shieldTime = 0;
          shieldOn = false;
        }
        
        if(doubleScoreTime > Constants.DOUBLE_SCORE_TIME) { // ダブルスコアの持続時間が一定時間を超えた場合、ダブルスコアをオフにする
          doubleScoreOn = false;
          doubleScoreTime = 0;
        }

        if(rapidFireTime > Constants.RAPID_FIRE_TIME) { // 連射弾の持続時間が一定時間を超えた場合、連射弾をオフにする
          rapidFireOn = false;
          rapidFireTime = 0;
        }

        if(doubleGunTime > Constants.DOUBLE_GUN_TIME) { // ダブルガンの持続時間が一定時間を超えた場合、ダブルガンをオフにする
          doubleGunOn = false;
          doubleGunTime = 0;
        }

        if(spawning) { // スポーン中の場合、点滅とスポーンの時間を更新
          flickerTime += dt;
          spawnTime += dt;

          if(flickerTime > Constants.FLICKER_TIME) { // 無敵時間が一定時間を超えた場合、表示/非表示を切り替える
            visible = !visible;
            flickerTime = 0;
          }

          if(spawnTime > Constants.SPAWNING_TIME) { // スポーンの時間が一定時間を超えた場合、スポーンを終了して表示する
            spawning = false;
            visible = true;
          }
        }

        // レーザーの発射間隔
        if(KeyBoard.SHOOT && fireRate > fireSpeed && !spawning) { // スペースキーが押されていて、レーザーの発射間隔が一定時間を超えていて、スポーン中でない場合にレーザーを発射
          if(doubleGunOn) { // ダブルガンがオンの場合、左右にレーザーを発射
            Vector2D leftGun = getCenter();
            Vector2D rightGun = getCenter();

            Vector2D temp = new Vector2D(heading); // プレイヤーの向きを表すベクトルをコピーして、レーザーの発射位置を計算するために使用
            temp.normalize(); // ベクトルを正規化して、レーザーの発射位置を計算するための単位ベクトルにする
            temp = temp.setDirection(angle - 1.3f); // ベクトルの方向をプレイヤーの向きから少しずらして、右側のレーザーの発射位置を計算
            temp = temp.scale(width); // ベクトルの大きさをプレイヤーの幅にスケーリングして、レーザーの発射位置を計算
            rightGun = rightGun.add(temp);

            temp = temp.setDirection(angle - 1.9f); // ベクトルの方向をプレイヤーの向きから少しずらして、左側のレーザーの発射位置を計算
            leftGun = leftGun.add(temp);

            Laser l = new Laser(leftGun, heading, Constants.LASER_VEL, angle, Assets.blueLaser, gameState); // 左側のレーザーを作成
            Laser r = new Laser(rightGun, heading, Constants.LASER_VEL, angle, Assets.blueLaser, gameState); // 右側のレーザーを作成

            gameState.getMovingObjects().add(0, l);
            gameState.getMovingObjects().add(0, r);
          }else { // ダブルガンがオフの場合、中央からレーザーを発射
            gameState.getMovingObjects().add(0, new Laser(getCenter().add(heading.scale(width)), heading, Constants.LASER_VEL, angle, Assets.blueLaser, gameState));
          }

          fireRate = 0;
          shoot.play();
        }

        if(shoot.getFramePosition() > 8500) { // レーザーの音が長すぎる場合、音を停止して再生位置をリセットする
          shoot.stop();
        }
        
        // 自機の移動
        if(KeyBoard.RIGHT)
          angle += Constants.DELTAANGLE;
        if(KeyBoard.LEFT)
          angle -= Constants.DELTAANGLE;

        if(KeyBoard.UP) {
          acceleration = heading.scale(Constants.ACC);
          accelerating = true;
          ACC.play();
        }else { // 加速キーが押されていない場合、減速して停止するようにする
          if(velocity.getMagnitude() != 0)
            acceleration = (velocity.scale(-1).normalize()).scale(Constants.ACC / 2);
          accelerating = false;
          ACC.stop();
        }
        
        velocity = velocity.add(acceleration);

        velocity = velocity.limit(maxVel);

        heading = heading.setDirection(angle - Math.PI / 2); // プレイヤーの向きを表すベクトルの方向をプレイヤーの角度に合わせて更新

        position = position.add(velocity);

        if(position.getX() > Constants.WIDTH) // 画面の右端を超えた場合、左端に移動する
          position.setX(0);
        if(position.getY() > Constants.HEIGHT) // 画面の下端を超えた場合、上端に移動する
          position.setY(0);

        if(position.getX() < 0) // 画面の左端を超えた場合、右端に移動する
          position.setX(Constants.WIDTH);
        if(position.getY() < 0) // 画面の上端を超えた場合、下端に移動する
          position.setY(Constants.HEIGHT);

        if(shieldOn) // シールドがオンの場合、シールドのアニメーションを更新
          shieldEffect.update(dt);  

        collidesWith();
    }

    public void setShield() {
        if(shieldOn)
          shieldTime = 0;
        shieldOn = true;  
    }

    public void setDoubleScore() {
        if(doubleScoreOn)
          doubleScoreTime = 0;
        doubleScoreOn = true;  
    }

    public void setRapidFire() {
        if(rapidFireOn)
          rapidFireTime = 0;
        rapidFireOn = true;
    }

    public void setDoubleGun() {
        if(doubleGunOn)
          doubleGunTime = 0;
        doubleGunOn = true;  
    }

    // プレイヤーが破壊されたときの処理
    @Override
    public void Destroy() {
        spawning = true;
        gameState.playExplosion(position);
        spawnTime = 0;
        lose.play();
        ACC.stop();
        if(!gameState.subtractLife(position)) { // 残機がなくなった場合、ゲームオーバーの処理を行う
          gameState.gameOver();
          super.Destroy();
        }
        resetValues();
    }

    // プレイヤーの値をリセットするためのメソッド
    private void resetValues() {
        angle = 0;
        velocity = new Vector2D();
        position = GameState.PLAYER_START_POSITION;
    }

    // プレイヤーの描画処理
    @Override
    public void draw(Graphics g) {
        if(!visible)
          return;

        Graphics2D g2d = (Graphics2D)g;

        // 左側エンジンの変換行列
        AffineTransform at1 = AffineTransform.getTranslateInstance(position.getX() + width/2 + 25, position.getY() + height/2 + 10);

        // 右側エンジンの変換行列
        AffineTransform at2 = AffineTransform.getTranslateInstance(position.getX() + 5, position.getY() + height/2 + 10); 

        at1.rotate(angle, -25, -10); // エンジンの画像をプレイヤーの向きに合わせて回転させるための変換行列の回転部分を設定
        at2.rotate(angle, width/2 -5, -10); // エンジンの画像をプレイヤーの向きに合わせて回転させるための変換行列の回転部分を設定

        if(accelerating) { // 加速している場合、エンジンの画像を描画
          g2d.drawImage(Assets.speed, at1, null);
          g2d.drawImage(Assets.speed, at2, null);
        }

        if(shieldOn) { // シールドがオンの場合、シールドのアニメーションを描画
          BufferedImage currentFrame = shieldEffect.getCurrentFrame();
          AffineTransform at3 = AffineTransform.getTranslateInstance(position.getX() - currentFrame.getWidth()/2 + width/2, 
                                                                     position.getY() - currentFrame.getHeight()/2 + height/2);

          // シールドの画像をプレイヤーの向きに合わせて回転させるための変換行列の回転部分を設定
          at3.rotate(angle, currentFrame.getWidth()/2, currentFrame.getHeight()/2);
          
          g2d.drawImage(shieldEffect.getCurrentFrame(), at3, null);
        }

        // プレイヤーの変換行列
        at = AffineTransform.getTranslateInstance(position.getX(), position.getY());

        // プレイヤーの画像をプレイヤーの向きに合わせて回転させるための変換行列の回転部分を設定
        at.rotate(angle, width / 2, height / 2);

        if(doubleGunOn) // ダブルガンがオンの場合、ダブルガンの画像を描画
          g2d.drawImage(Assets.doubleGunPlayer, at, null);
        else // ダブルガンがオフの場合、通常のプレイヤーの画像を描画
          g2d.drawImage(texture, at, null);  
    }

    public boolean isSpawning() {
        return spawning;
    }

    public boolean isShieldOn() {
        return shieldOn;
    }

    public boolean isDoubleScoreOn() {
        return doubleScoreOn;
    }
}