import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

// ローディング画面の状態を表すクラス
public class LoadingState extends State {
    
    private Thread loadingThread; // ローディングスレッドを保持する変数

    private Font font;

    public LoadingState(Thread loadingThread) {
        this.loadingThread = loadingThread;
        this.loadingThread.start();
        font = Loader.loadFont("/resources/fonts/arcadeFont.ttf", 38);
    }
    @Override
    public void update(float dt) {
        // ロードが完了したらメインメニューに遷移
        if(Assets.loaded) {
            State.changeState(new MenuState());
            try {
                loadingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        // ローディングバーのグラデーションを設定
        GradientPaint gp = new GradientPaint(Constants.WIDTH/2 - Constants.LOADING_BAR_WIDTH/2, 
                                             Constants.HEIGHT/2 - Constants.LOADING_BAR_HEIGHT/2, 
                                             Color.WHITE,
                                             Constants.WIDTH/2 + Constants.LOADING_BAR_WIDTH/2, 
                                             Constants.HEIGHT/2 + Constants.LOADING_BAR_HEIGHT/2, 
                                             Color.BLUE);

        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setPaint(gp);

        float percentage = (Assets.count / Assets.MAX_COUNT); // ロードの進行状況を0から1の範囲で計算

        // ローディングバーの描画
        g2d.fillRect(Constants.WIDTH/2 - Constants.LOADING_BAR_WIDTH/2, 
                     Constants.HEIGHT/2 - Constants.LOADING_BAR_HEIGHT/2, 
                     (int)(Constants.LOADING_BAR_WIDTH * percentage), Constants.LOADING_BAR_HEIGHT);
        
        // ローディングバーの枠を描画
        g2d.drawRect(Constants.WIDTH/2 - Constants.LOADING_BAR_WIDTH/2, 
                     Constants.HEIGHT/2 - Constants.LOADING_BAR_HEIGHT/2, 
                     Constants.LOADING_BAR_WIDTH, Constants.LOADING_BAR_HEIGHT);
                      
        // ローディングテキストの描画             
        Text.drawText(g2d, "LOADING...", new Vector2D(Constants.WIDTH/2, Constants.HEIGHT/2 - 40), 
                      true, Color.WHITE, font);              
    }
    
}