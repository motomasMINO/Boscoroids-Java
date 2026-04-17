import javax.swing.filechooser.FileSystemView;

// ゲーム全体で使用する定数をまとめたクラス
public class Constants {
    // フレーム寸法
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;

    // 自機プロパティ
    public static final int FIRERATE = 250; // 自機のレーザー発射間隔
    public static final double DELTAANGLE = 0.1; // 自機の回転速度
    public static final double ACC = 0.2; // 自機の移動速度
    public static final double PLAYER_MAX_VEL = 7.0; // 自機の最大速度
    public static final long FLICKER_TIME = 200; // 自機がダメージを受けたときの無敵時間
    public static final long SPAWNING_TIME = 3000; // 自機がスポーンしてから無敵になるまでの時間
    public static final long GAME_OVER_TIME = 3000; // ゲームオーバーになってからタイトル画面に戻るまでの時間

    // レーザープロパティ
    public static final double LASER_VEL = 15.0; // 自機のレーザー速度
    public static final double UFO_LASER_VEL = 5.0; // UFOのレーザー速度

    // 隕石プロパティ
    public static final double METEOR_INIT_VEL = 2.0; // 隕石の初期速度
    public static final int METEOR_SCORE = 20; // 隕石のスコア
    public static final double METEOR_MAX_VEL = 6.0; // 隕石の最大速度
    public static final int SHIELD_DISTANCE = 150; // シールドが隕石を破壊する距離
    
    // UFOプロパティ
    public static final int NODE_RADIUS = 160; // UFOが自機を追尾する際のノードの半径
    public static final double UFO_MASS = 60; // UFOの質量
    public static final int UFO_MAX_VEL = 3; // UFOの最大速度
    public static long UFO_FIRE_RATE = 1000; // UFOのレーザー発射間隔
    public static double UFO_ANGLE_RANGE = Math.PI / 2; // UFOが自機を追尾する際の角度範囲
    public static final int UFO_SCORE = 40; // UFOのスコア
    public static final long UFO_SPAWN_RATE = 10000; // UFOの出現間隔
    public static final String PLAY = "PLAY";
    public static final String QUIT = "QUIT";

    // ローディングバー プロパティ
    public static final int LOADING_BAR_WIDTH = 500;
    public static final int LOADING_BAR_HEIGHT = 50;

    // ボタン表記
    public static final String RETURN = "RETURN";
    public static final String HIGH_SCORES = "HI-SCORES";

    // スコア表
    public static final String SCORE = "SCORE";
    public static final String DATE = "DATE";

    // スコア保存用のファイルパス
    public static final String SCORE_PATH = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\ボスコロイド\\data.json";

    // XMLParserを使用するためにはこの変数が必要
    public static final String PLAYER = "PLAYER";
    public static final String PLAYERS = "PLAYERS";

    // =============================================

    // 各パワーアップアイテムの出現間隔
    public static final long POWER_UP_DURATION = 10000; // パワーアップの効果時間
    public static final long POWER_UP_SPAWN_TIME = 8000; // パワーアップアイテムの出現間隔

    public static final long SHIELD_TIME = 12000; // シールドの効果時間
    public static final long DOUBLE_SCORE_TIME = 10000; // ダブルスコアの効果時間
    public static final long RAPID_FIRE_TIME = 14000; // 連射弾の効果時間
    public static final long DOUBLE_GUN_TIME = 12000; // ダブルガンの効果時間

    public static final int SCORE_STACK = 1000; // スコアがこの値を超えるごとにパワーアップアイテムが出現
}