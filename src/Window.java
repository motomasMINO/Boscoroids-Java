import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.*;

// ゲームのウィンドウを作成するクラス(兼実行用クラス)
public class Window extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;

    private Canvas canvas;
    private Thread thread; // ゲームループを実行するスレッド
    private boolean running = false; // ゲームループが実行中かどうかを示すフラグ

    private BufferStrategy bs; // 描画のためのバッファ
    private Graphics g;

    private final int FPS = 60; // 秒間60FPS表示
    private double TARGETTIME = 1000000000/FPS;
    private double delta = 0; // ゲームループの更新と描画のタイミングを管理するための変数
    private int AVERAGEFPS = FPS; // 平均FPSを表示

    private KeyBoard keyBoard;
    private MouseInput mouseInput;

    public Window() {
        setTitle("BOSCOROIDS"); // ウィンドウ上部に表示するタイトル
        setSize(Constants.WIDTH, Constants.HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ウィンドウを閉じるとプログラムが終了
        setResizable(false); // ウィンドウのサイズ変更不可
        setLocationRelativeTo(null); // ウィンドウを画面中央に表示

        canvas = new Canvas();
        keyBoard = new KeyBoard();
        mouseInput = new MouseInput();

        // キャンバスのサイズを設定し、イベントリスナーを追加
        canvas.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setMaximumSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setMinimumSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
        canvas.setFocusable(true);
        
        add(canvas);
        canvas.addKeyListener(keyBoard);
        canvas.addMouseListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
        setVisible(true); // ウィンドウの可視化
    }
    public static void main(String[] args) { // プログラムのエントリーポイント
        new Window().start();
    }
    
    // ゲームループの更新
    private void update(float dt) {  
        keyBoard.update();
        State.getCurrentState().update(dt);
    }

    // ゲームループの描画
    private void draw() {
        bs = canvas.getBufferStrategy(); // バッファを取得
        
        if(bs == null) { // バッファが存在しない場合は作成してから描画を行う
            canvas.createBufferStrategy(3); // 3重バッファリングを使用
            return;
        }

        g = bs.getDrawGraphics(); // 描画用のGraphicsオブジェクトを取得

        // ----------------------

        g.setColor(Color.BLACK); // 背景の色

        g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        State.getCurrentState().draw(g);

        g.setColor(Color.WHITE);
        
        g.drawString("" + AVERAGEFPS, 10, 20); // FPSを数値化して表示

        // ----------------------

        g.dispose(); // 描画用のGraphicsオブジェクトを解放
        bs.show(); // バッファを画面に表示
    }

    // ゲームの初期化
    private void init() {
        Thread loadingThread = new  Thread(new Runnable() {

            // Assetsクラスの初期化を別スレッドで行うことで、ロード中にローディング画面を表示
            @Override
            public void run() {
                Assets.init();
            }
        });

        State.changeState(new LoadingState(loadingThread)); // ローディングスレッドを渡してローディング画面を表示
    }

    // ゲームループを実行
    @Override
    public void run() {
        
        long now = 0;
        long lastTime = System.nanoTime();
        int frames = 0;
        long time = 0;

        init();

        // ゲームループの開始
        while(running) {
           now = System.nanoTime();
           delta += (now - lastTime) / TARGETTIME;
           time += (now - lastTime);
           lastTime = now;

           // deltaが1以上になったら、ゲームの更新と描画を行う
           if(delta >= 1) {
             update((float) (delta * TARGETTIME * 0.000001f));
             draw();
             delta --;
             frames ++;
           }
           if(time >= 1000000000) { // 1秒経過したら平均FPSを更新
             AVERAGEFPS = frames;
             frames = 0;
             time = 0;
           }

        }
        stop();
    }
    
    // ゲームループの開始と停止を管理するメソッド
    private void start() {
        
        thread = new Thread(this);
        thread.start();
        running = true;

    }

    // ゲームループの停止
    private void stop() {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}