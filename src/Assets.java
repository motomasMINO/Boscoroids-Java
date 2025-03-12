import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.sound.sampled.Clip;

public class Assets {

   public static boolean loaded = false;
   public static float count = 0;
   public static float MAX_COUNT = 57;

    // 自機
    public static BufferedImage player;
    public static BufferedImage doubleGunPlayer;
    
    // エフェクト
    public static BufferedImage speed;
    public static BufferedImage[]shieldEffect = new BufferedImage[3];

    // 爆発
    public static BufferedImage[] exp = new BufferedImage[9];

    // レーザー
    public static BufferedImage blueLaser, greenLaser, redLaser;

    // 隕石
    public static BufferedImage[] bigs = new BufferedImage[4];
    public static BufferedImage[] meds = new BufferedImage[2];
    public static BufferedImage[] smalls = new BufferedImage[2];
    //public static BufferedImage[] tinies = new BufferedImage[2];

    // UFO
    public static BufferedImage ufo;

    // スコアと残機
    public static BufferedImage[] numbers = new BufferedImage[11];
    public static BufferedImage life;

    // フォント
    public static Font fontBig;
    public static Font fontMed;

    // サウンド
    public static Clip accelerationMusic, explosion, playerLose, playerShoot, ufoDestroy, backgroundMusic, powerUp;

    // UI
    public static BufferedImage blueBtn;
    public static BufferedImage greyBtn;

    // パワーアップ
    public static BufferedImage orb, luckyFrag, doubleGun, rapidFire, shield, pacman, specialFlag;

    public static void init() {

        // キャラ画像読み込み
        player = Loader.ImageLoader("/resources/images/Star_Fighter_red.png");
        doubleGunPlayer = Loader.ImageLoader("/resources/images/Star_Fighter_red2.png");
        speed = Loader.ImageLoader("/resources/images/fire08.png");
        blueLaser = Loader.ImageLoader("/resources/images/laserBlue01.png");
        greenLaser = Loader.ImageLoader("/resources/images/laserGreen11.png");
        redLaser = Loader.ImageLoader("/resources/images/laserRed01.png"); 
        ufo = Loader.ImageLoader("/resources/images/ufo_yellow.png");
        life = Loader.ImageLoader("/resources/images/life.png");

        // フォント読み込み
        fontBig = Loader.loadFont("/resources/fonts/arcadeFont.ttf", 42);
        fontMed = Loader.loadFont("/resources/fonts/arcadeFont.ttf", 20);

        // シールド画像読み込み
        for(int i = 0; i < 3; i++)
           shieldEffect[i] = loadImage("/resources/images/shield" + (i + 1) + ".png");

        // 隕石画像読み込み
        for(int i = 0; i < bigs.length; i++)
           bigs[i] = Loader.ImageLoader("/resources/images/big" + (i + 1) + ".png");

        for(int i = 0; i < meds.length; i++)
           meds[i] = Loader.ImageLoader("/resources/images/med" + (i + 1) + ".png");

        for(int i = 0; i < smalls.length; i++)
           smalls[i] = Loader.ImageLoader("/resources/images/small" + (i + 1) + ".png");
           
        //for(int i = 0; i < tinies.length; i++)
           //tinies[i] = Loader.ImageLoader("./tiny" + (i + 1) + ".png");
        
        // 爆発画像読み込み
        for(int i = 0; i < exp.length; i++)
           exp[i] = Loader.ImageLoader("/resources/images/" + "exp" + i + ".png");
        
        // スコア数字画像読み込み   
        for(int i = 0; i < numbers.length; i++)
           numbers[i] = Loader.ImageLoader("/resources/images/" + i + ".png");

        // サウンド読み込み   
        accelerationMusic = Loader.loadSound("/resources/sounds/playerACC.wav");
        explosion = Loader.loadSound("/resources/sounds/explosion.wav");
        playerLose = Loader.loadSound("/resources/sounds/playerLose.wav");
        playerShoot = Loader.loadSound("/resources/sounds/playerShoot.wav");
        ufoDestroy = Loader.loadSound("/resources/sounds/ufoDestroy.wav");
        backgroundMusic = Loader.loadSound("/resources/sounds/backgroundMusic.wav");
        powerUp = Loader.loadSound("/resources/sounds/SpecialFlag_Get.wav");

        // ボタン画像読み込み
        greyBtn = Loader.ImageLoader("/resources/images/grey_button.png");
        blueBtn = Loader.ImageLoader("/resources/images/blue_button.png");

        // パワーアップ画像読み込み
        orb = loadImage("/resources/images/orb.png");
        luckyFrag = loadImage("/resources/images/lucky-flag.png");
        doubleGun = loadImage("/resources/images/doubleGun.png");
        rapidFire = loadImage("/resources/images/rapidFire.png");
        pacman = loadImage("/resources/images/pacman.png");
        shield = loadImage("/resources/images/barrier.png");
        specialFlag = loadImage("/resources/images/special-flag.png");

        // =================================================================

        loaded = true;

    }

    public static BufferedImage loadImage(String path) {
        count ++;
        return Loader.ImageLoader(path);
    }

    public static Font loadFont(String path, int size) {
        count ++;
        return Loader.loadFont(path, size);
    }

    public static Clip loadSound(String path) {
        count ++;
        return Loader.loadSound(path);
    }
}
