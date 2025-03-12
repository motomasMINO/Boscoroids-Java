import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class Window extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;

    private Canvas canvas;
    private Thread thread;
    private boolean running = false;

    private BufferStrategy bs;
    private Graphics g;

    private final int FPS = 60; // 秒間60FPS表示
    private double TARGETTIME = 1000000000/FPS;
    private double delta = 0;
    private int AVERAGEFPS = FPS;

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
    public static void main(String[] args) {
        new Window().start();
    }
    
    private void update(float dt) {  
        keyBoard.update();
        State.getCurrentState().update(dt);
    }

    private void draw() {
        bs = canvas.getBufferStrategy();
        
        if(bs == null) {
            canvas.createBufferStrategy(3);
            return;
        }

        g = bs.getDrawGraphics();

        // ----------------------

        g.setColor(Color.BLACK); // 背景の色

        g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        State.getCurrentState().draw(g);

        g.setColor(Color.WHITE);
        
        g.drawString("" + AVERAGEFPS, 10, 20); // FPSを数値化して表示

        // ----------------------

        g.dispose();
        bs.show();
    }

    private void init() {
        Thread loadingThread = new  Thread(new Runnable() {

            @Override
            public void run() {
                Assets.init();
            }
        });

        State.changeState(new LoadingState(loadingThread));
    }

    @Override
    public void run() {
        
        long now = 0;
        long lastTime = System.nanoTime();
        int frames = 0;
        long time = 0;

        init();

        while(running) {
           now = System.nanoTime();
           delta += (now - lastTime) / TARGETTIME;
           time += (now - lastTime);
           lastTime = now;

           if(delta >= 1) {
             update((float) (delta * TARGETTIME * 0.000001f));
             draw();
             delta --;
             frames ++;
           }
           if(time >= 1000000000) {
             AVERAGEFPS = frames;
             frames = 0;
             time = 0;
           }

        }
        stop();
    }
    
    private void start() {
        
        thread = new Thread(this);
        thread.start();
        running = true;

    }

    private void stop() {
        try {
            thread.join();
            running = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}