import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

// ゲームの状態を管理するクラス
public class GameState extends State {
    public static final Vector2D PLAYER_START_POSITION = new Vector2D(Constants.WIDTH/2 - Assets.player.getWidth()/2, 
                                                                      Constants.HEIGHT/2 - Assets.player.getHeight()/2);
    
    private Player player;
    private ArrayList<MovingObject> movingObjects = new ArrayList<MovingObject>();
    private ArrayList<Animation> explosions = new ArrayList<Animation>();
    private ArrayList<Message> messages = new ArrayList<Message>();

    private int score = 0;
    private int lives = 3;

    private int meteors;
    private int rounds = 1;

    private Sound backgroundMusic;
    private long gameOverTimer; // ゲームオーバーになってからメニューに遷移するまでの時間を計測するためのタイマー
    private boolean gameOver;

    private long ufoSpawner; // UFOをスポーンさせるためのタイマー
    private long powerUpSpawner; // パワーアップアイテムをスポーンさせるためのタイマー

    public GameState() {
        player = new Player(PLAYER_START_POSITION, new Vector2D(), Constants.PLAYER_MAX_VEL, Assets.player, this); // 自機の初期位置

        gameOver = false;
        movingObjects.add(player); // 自機を移動オブジェクトのリストに追加

        meteors = 1; // 最初は隕石1つからスタート
        startRound();
        backgroundMusic = new Sound(Assets.backgroundMusic);
        backgroundMusic.loop();
        backgroundMusic.changeVolume(-3.0f);

        gameOverTimer = 0;
        ufoSpawner = 0;
        powerUpSpawner = 0;

        gameOver = false;
    }

    // スコアを加算するメソッド
    public void addScore(int value, Vector2D position) {
        Color c = Color.WHITE;
        String text = "+" + value + " POINTS";
        if(player.isDoubleScoreOn()) { // ダブルスコアが有効な場合、スコアを2倍にして黄色い文字で表示
            c = Color.YELLOW;
            value = value * 2;
            text = "+" + value + " POINTS" + "(X2)";
        }
        score += value;
        messages.add(new Message(position, true, text, c, false, Assets.fontMed));
    }

    // 隕石を分割するメソッド
    public void divideMeteor(Meteor meteor) {
        Size size = meteor.getSize(); // 隕石のサイズを取得

        BufferedImage[] textures = size.textures; // 隕石のサイズに応じたテクスチャの配列を取得
        
        Size newSize = null; // 分割後の隕石のサイズを格納する変数

        // 隕石のサイズに応じて分割後の隕石のサイズを決定する
        switch(size) {
        case BIG:
            newSize = Size.MED;
            break;
        case MED:
            newSize = Size.SMALL;
            break;
        //case SMALL:
            //newSize = Size.TINY;
            //break;
        default:
            return;            
        }

        // 分割後の隕石を生成して移動オブジェクトのリストに追加する
        for(int i = 0; i < size.quantity; i++) {
            movingObjects.add(new Meteor(meteor.getPosition(), 
                                         new Vector2D(0, 1).setDirection(Math.random() * Math.PI * 2),
                              Constants.METEOR_INIT_VEL * Math.random() + 1,
                              textures[(int) (Math.random() * textures.length)], this, newSize)); 
        }
    }

    // ラウンドを開始するメソッド
    private void startRound() {
        messages.add(new Message(new Vector2D(Constants.WIDTH/2, Constants.HEIGHT/2), 
                     false, "ROUND " + rounds, Color.WHITE, true, Assets.fontBig));

        double x, y;

        // ラウンド数に応じて隕石の数を増やしていく
        for(int i = 0; i < meteors; i++) {
           x = i % 2 == 0 ? Math.random() * Constants.WIDTH : 0;
           y = i % 2 == 0 ? 0 : Math.random() * Constants.HEIGHT;
           
           BufferedImage texture = Assets.bigs[(int)(Math.random() * Assets.bigs.length)];

           movingObjects.add(new Meteor(new Vector2D(x, y), 
                                        new Vector2D(0, 1).setDirection(Math.random() * Math.PI * 2),
                             Constants.METEOR_INIT_VEL * Math.random() + 1,
                             texture, this, Size.BIG));
        }
        meteors ++;
        rounds ++;
    }

    // 爆発アニメーションを再生するメソッド
    public void playExplosion(Vector2D position) {
        explosions.add(new Animation(Assets.exp, 50, 
        position.subtract(new Vector2D(Assets.exp[0].getWidth()/2, Assets.exp[0].getHeight()/2))));
    }

    // UFOをスポーンさせるメソッド
    public void spawnUfo() {
        int rand = (int) (Math.random() * 2); // 0か1をランダムに生成して、UFOの出現位置を決定する

        // randが0の場合は画面上部から、1の場合は画面右側から出現させる
        double x = rand == 0 ? (Math.random() * Constants.WIDTH): Constants.WIDTH;
        double y = rand == 0 ? Constants.HEIGHT : (Math.random() * Constants.HEIGHT);

        ArrayList<Vector2D> path = new ArrayList<Vector2D>(); // UFOの移動パスを格納するリスト

        double posX, posY; // UFOの移動パスにランダムな位置を4点追加する

        // UFOは画面の4象限すべてに出現する可能性があるため、ランダムな位置を追加して移動パターンにバリエーションを持たせる
        posX = Math.random() * Constants.WIDTH / 2;
        posY = Math.random() * Constants.HEIGHT / 2;
        path.add(new Vector2D(posX, posY));

        // UFOが画面の右側から出現する場合は、左側に移動する
        posX = Math.random() * (Constants.WIDTH / 2) + Constants.WIDTH / 2;
        posY = Math.random() * Constants.HEIGHT / 2;
        path.add(new Vector2D(posX, posY));
        
        // UFOが画面の下側から出現する場合は、上側に移動する
        posX = Math.random() * Constants.WIDTH / 2;
        posY = Math.random() * (Constants.HEIGHT / 2) + Constants.HEIGHT / 2;
        path.add(new Vector2D(posX, posY));

        // UFOが画面の左側から出現する場合は、右側に移動する
        posX = Math.random() * (Constants.WIDTH / 2) + Constants.WIDTH / 2;
        posY = Math.random() * (Constants.HEIGHT / 2) + Constants.HEIGHT / 2;
        path.add(new Vector2D(posX, posY));

        // UFOを移動オブジェクトのリストに追加する
        movingObjects.add(new Ufo(new Vector2D(x, y), new Vector2D(), Constants.UFO_MAX_VEL, Assets.ufo, path, this));
    }

    // パワーアップアイテムをスポーンさせるメソッド
    public void spawnPowerUp() {
        // パワーアップアイテムは画面内のランダムな位置に出現させる
        final int x = (int) ((Constants.WIDTH - Assets.orb.getWidth()) * Math.random());
        final int y = (int) ((Constants.HEIGHT - Assets.orb.getHeight()) * Math.random());

        int index = (int) (Math.random() * (PowerUpTypes.values().length)); // PowerUpTypesの列挙型からランダムにパワーアップの種類を選択する

        PowerUpTypes p = PowerUpTypes.values()[index]; // ランダムに選択されたパワーアップの種類を取得する
        
        final String text = p.text; // パワーアップの種類に応じたテキストを取得する
        Action action = null; // パワーアップの種類に応じたアクションを定義するための変数
        Vector2D position = new Vector2D(x, y); // パワーアップアイテムの位置を表すベクトルを作成する

        // パワーアップの種類に応じて、実行されるアクションを定義する
        switch(p) {
        case LIFE:
                action = new Action() {
                    @Override
                    public void doAction() {
                        lives ++;
                        messages.add(new Message(position, false, text, Color.GREEN, false, Assets.fontMed));
                    }
                };
                break;
        case SHIELD:
                action = new Action() {
                    @Override
                    public void doAction() {
                        player.setShield();
                        messages.add(new Message(position, false, text, Color.DARK_GRAY, false, Assets.fontMed));
                    }
                };
                break;
        case SCORE_X2:
                action = new Action() {
                    @Override
                    public void doAction() {
                        player.setDoubleScore();
                        messages.add(new Message(position, false, text, Color.YELLOW, false, Assets.fontMed));
                    }
                };
                break;
        case RAPID_FIRE:
                action = new Action() {
                    @Override
                    public void doAction() {
                        player.setRapidFire();
                        messages.add(new Message(position, false, text, Color.BLUE, false, Assets.fontMed));
                    }
                };
                break;
        case SCORE_STACK:
                action = new Action() {
                    @Override
                    public void doAction() {
                        score += 1000;
                        messages.add(new Message(position, false, text, Color.MAGENTA, false, Assets.fontMed));
                    }
                };
                break;
        case DOUBLE_GUN:
                action = new Action() {
                    @Override
                    public void doAction() {
                        player.setDoubleGun();
                        messages.add(new Message(position, false, text, Color.ORANGE, false, Assets.fontMed));
                    }
                };
                break;
        default:
                break;                                                    
        }

        this.movingObjects.add(new PowerUp(position, p.texture, action, this));
    }

    // ゲームの状態を更新
    public void update(float dt) {
        if(gameOver) // ゲームオーバーになっている場合は、ゲームオーバーからメニューに遷移するまでの時間を計測するためのタイマーを更新する
          gameOverTimer += dt;

          powerUpSpawner += dt;
          ufoSpawner += dt;

        // 移動オブジェクトのリストをループして、各オブジェクトの状態を更新する
        for(int i = 0; i < movingObjects.size(); i++) {
            
            MovingObject mo = movingObjects.get(i);
           
            mo.update(dt);
            if(mo.isDead()) { // オブジェクトが死んでいる場合は、移動オブジェクトのリストから削除する
                movingObjects.remove(i);
                i--;
            }
        }
        // 爆発アニメーションのリストをループして、各アニメーションの状態を更新する   
        for(int i = 0; i < explosions.size(); i++){
           Animation anim = explosions.get(i);
           anim.update(dt);
           if(!anim.isRunning()) { // アニメーションが終了している場合は、爆発アニメーションのリストから削除する
             explosions.remove(i);
           }   
        }

        // ゲームオーバーになってから一定時間が経過したら、メニューに遷移する
        if(gameOverTimer > Constants.GAME_OVER_TIME) {

            try {
                ArrayList<ScoreData> dataList = JSONParser.readFile();
                dataList.add(new ScoreData(score));
                JSONParser.writeFile(dataList);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            State.changeState(new MenuState());
            backgroundMusic.stop();
        }

        // パワーアップアイテムとUFOをスポーンさせるためのタイマーを更新して、一定時間が経過したらそれぞれスポーンさせる
        if(powerUpSpawner > Constants.POWER_UP_SPAWN_TIME) {
            spawnPowerUp();
            powerUpSpawner = 0;
        }

        // UFOをスポーンさせるためのタイマーを更新して、一定時間が経過したらUFOをスポーンさせる
        if(ufoSpawner > Constants.UFO_SPAWN_RATE) {
            spawnUfo();
            ufoSpawner = 0;
        }

           // ラウンドをクリアしたかどうかをチェックするために、移動オブジェクトのリストをループして、隕石が残っているかどうかを確認する
           for(int i = 0; i < movingObjects.size(); i++)
              if(movingObjects.get(i) instanceof Meteor)
                return;
                
           startRound();     
    }

    // ゲームの状態を描画
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        // 描画の品質を向上させるためのレンダリングヒントを設定する
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // メッセージのリストをループして、各メッセージを描画する。メッセージが死んでいる場合は、メッセージのリストから削除する
        for(int i = 0; i < messages.size(); i++) {
            messages.get(i).draw(g2d);
            if(messages.get(i).isDead())
              messages.remove(i);
        }
        
        // 移動オブジェクトのリストをループして、各オブジェクトを描画する
        for(int i = 0; i < movingObjects.size(); i++)
        movingObjects.get(i).draw(g);

        // 爆発アニメーションのリストをループして、各アニメーションを描画する
        for(int i = 0; i < explosions.size(); i++){
            Animation anim = explosions.get(i);
            g2d.drawImage(anim.getCurrentFrame(), (int)anim.getPosition().getX(), (int)anim.getPosition().getY(), null);
        }
        drawScore(g);
        drawLives(g);
    }

    // スコアを描画するメソッド
    private void drawScore(Graphics g) {
        Vector2D pos = new Vector2D(850, 25);

        String scoreToString = Integer.toString(score); // スコアを文字列に変換して、各桁をループして描画する

        // スコアを文字列に変換して、各桁をループして描画する
        for(int i = 0; i < scoreToString.length(); i++) {
           g.drawImage(Assets.numbers[Integer.parseInt(scoreToString.substring(i, i + 1))], 
                   (int)pos.getX(), (int)pos.getY(), null);
           pos.setX(pos.getX() + 20);
        }
    }

    // 残機を描画するメソッド
    private void drawLives(Graphics g) {
        // 残機が0未満の場合は、残機を描画せずにメソッドを終了する
        if(lives < 1)
          return;

        Vector2D livePosition = new Vector2D(25, 25); // 残機を描画する位置を表すベクトルを作成

        g.drawImage(Assets.life, (int)livePosition.getX(), (int)livePosition.getY(), null);

        g.drawImage(Assets.numbers[10], (int)livePosition.getX() + 40, (int)livePosition.getY() + 5, null);

        String livesToString = Integer.toString(lives); // 残機数を文字列に変換して、各桁をループして描画する

        Vector2D pos = new Vector2D(livePosition.getX(), livePosition.getY()); // 残機数を描画する位置を表すベクトルを作成

        // 残機数を文字列に変換して、各桁をループして描画する
        for(int i = 0; i < livesToString.length(); i++) {
           int number = Integer.parseInt(livesToString.substring(i, i + 1));
           
           // 残機数が0以下の場合は、残機数を描画せずにループを終了する
           if(number <= 0)
             break;
           g.drawImage(Assets.numbers[number], (int)pos.getX() + 60, (int)pos.getY() + 5, null);
           pos.setX(pos.getX() + 20);  // 次の桁を描画する位置を更新する
        }
    }

    // ゲームの状態を管理するためのゲッター
    public ArrayList<MovingObject> getMovingObjects() {
        return movingObjects;
    }

    // メッセージのリストを取得するためのゲッター
    public ArrayList<Message> getMessages() {
        return messages;
    }

    // プレイヤーオブジェクトを取得するためのゲッター
    public Player getPlayer() {
        return player;
    }

    // 残機を減らすメソッド。残機が0以下になった場合は、ゲームオーバーにする
    public boolean subtractLife(Vector2D position) {
        lives --;

        Message lifeLostMesg = new Message(position, false, "-1 LIFE", Color.RED, false, Assets.fontMed);
        messages.add(lifeLostMesg);

        return lives > 0;
    }

    // ゲームオーバーにするメソッド。ゲームオーバーのメッセージを表示して、ゲームオーバーの状態にする
    public void gameOver() {
        Message gameOverMsg = new Message(PLAYER_START_POSITION, true, "GAME OVER", Color.RED, true, Assets.fontBig);

        this.messages.add(gameOverMsg);
        gameOver = true;
    }
}
